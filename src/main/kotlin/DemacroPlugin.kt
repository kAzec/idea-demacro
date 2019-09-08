import com.intellij.configurationStore.APP_CONFIG
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.BaseComponent
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.PluginId
import org.jdom.Element

@State(name = "DemacroSettings", storages = [Storage(file = "$APP_CONFIG$/demacro_settings.xml")])
class DemacroPlugin private constructor() : BaseComponent, PersistentStateComponent<Element> {
    companion object {
        const val version = 1

        val shared: DemacroPlugin
            get() = ApplicationManager.getApplication().getComponent(DemacroPlugin::class.java)

        val pluginId = PluginId.getId("Demacro")
    }

    var store = DemacroStore()
        set(value) {
            field = value
            reloadDemacros()
        }

    var previousVersion = -1
        private set

    override fun initComponent() {
        log.info("Plugin started.")
        reloadDemacros()
    }

    override fun getState(): Element {
        log.debug("Serializing plugin state.")

        val meta = Element("meta").apply {
            setAttribute("version", DemacroPlugin.version.toString())
        }

        val demacros = Element("demacros").apply {
            store.serializeInto(this)
        }

        return Element("demacro").apply {
            addContent(meta)
            addContent(demacros)
        }
    }

    override fun loadState(state: Element) {
        log.debug("De-serializing plugin state.")

        state.getChild("meta")?.let {
            previousVersion = it.getAttributeValue("version")?.toIntOrNull() ?: -1
        }

        store.deserializeFrom(state.getChild("demacros"))
    }

    override fun noStateLoaded() {
        log.debug("Initial load, using default config.")
        store.deserializeFrom(null)
    }

    private fun reloadDemacros() {
        store.demacros.filter { it.isEnabled }.apply {
            // Unregistering previous actions.
            val actionManager = ActionManager.getInstance()
            actionManager.getActionIds(DemacroAction.idPrefix).forEach {
                actionManager.unregisterAction(it)
            }

            // Remove previous action group.
            val toolsMenuAction = (actionManager.getAction("ToolsMenu") as? DefaultActionGroup) ?: return
            toolsMenuAction.childActionsOrStubs.find { it.templatePresentation.text == "Demacro" }?.let {
                toolsMenuAction.remove(it)
            }

            if (isEmpty()) return

            log.info("Registering ${this.count()} saved demacro(s).")

            map {
                DemacroAction(it)
            }.apply {
                val demacroGroupAction = DefaultActionGroup("Demacro", this)
                demacroGroupAction.isPopup = true

                toolsMenuAction.add(demacroGroupAction, actionManager)
            }.forEach {
                actionManager.registerAction(it.id, it, DemacroPlugin.pluginId)
            }
        }
    }
}

private val log = Logger.getInstance(DemacroPlugin::class.java)