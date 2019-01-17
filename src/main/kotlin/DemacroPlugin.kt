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

            if (enabled) {
                reloadDemacros()
            }
        }

    var enabled = true
        private set

    var previousVersion = -1
        private set

    override fun initComponent() {
        log.info("Plugin started.")

        if (enabled) {
            reloadDemacros()
        }
    }

    override fun getState(): Element {
        log.debug("Serializing plugin state.")

        val element = Element("demacro")
        val meta = Element("plugin").let {
            it.setAttribute("isEnabled", enabled.toString())
            it.setAttribute("version", DemacroPlugin.version.toString())
        }
        element.addContent(meta)

        element.addContent(Element("demacros")).let {
            store.serializeInto(it)
        }
        return element
    }

    override fun loadState(state: Element) {
        log.debug("Deserializing plugin state.")

        state.getChild("plugin")?.let {
            enabled = it.getAttributeValue("isEnabled")?.toBoolean() ?: true
            previousVersion = it.getAttributeValue("version")?.toIntOrNull() ?: -1
        }

        store.deserializeFrom(state.getChild("demacros"))
    }

    override fun noStateLoaded() {
        store.deserializeFrom(null)
    }

    private fun reloadDemacros() {
        store.demacros.filter { it.isEnabled }.apply {
            // Unregistering previous actions.
            val actionManager = ActionManager.getInstance()
            actionManager.getActionIds("Demacro#").forEach {
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
                actionManager.registerAction("Demacro#$it.name", it, DemacroPlugin.pluginId)
            }
        }
    }
}

private val log = Logger.getInstance(DemacroPlugin::class.java)