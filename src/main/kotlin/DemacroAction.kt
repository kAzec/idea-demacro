import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl
import com.intellij.openapi.editor.impl.EditorImpl

class DemacroAction(private val demacro: Demacro) : AnAction(demacro.name) {
    override fun actionPerformed(e: AnActionEvent) {
        if (!demacro.isEnabled) {
            e.presentation.isEnabled = false
            return
        }

        val dataContext = (e.getData(PlatformDataKeys.EDITOR) as? EditorImpl)?.dataContext

        if (dataContext == null) {
            e.presentation.isEnabled = false
            return
        }

        val presentation = e.presentation
        val actionManager = ActionManagerImpl.getInstance()

        demacro.steps.forEach {
            val action = actionManager.getAction(it.action)
            val event = AnActionEvent(
                null,
                dataContext,
                it.action,
                presentation,
                actionManager,
                it.modifiers
            )

            repeat(it.times) {
                action.actionPerformed(event)
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val hasEditor = e.getData(PlatformDataKeys.EDITOR) as? EditorImpl != null
        e.presentation.isEnabled = hasEditor && demacro.isEnabled
        e.presentation.isVisible = demacro.isEnabled
    }
}