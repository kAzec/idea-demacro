import com.intellij.openapi.actionSystem.IdeActions
import org.jdom.Element

class DemacroStore {
    var demacros: List<Demacro>
        get() = mutableDemacros.toList()
        set(value) {
            mutableDemacros = value.toMutableList()
        }

    private var mutableDemacros = mutableListOf<Demacro>()

    fun serializeInto(element: Element) {
        for (demacro in mutableDemacros) {
            element.addContent(Element("demacro").apply {
                setAttribute("name", demacro.name)
                setAttribute("enabled", demacro.isEnabled.toString())

                for (step in demacro.steps) {
                    addContent(Element("step").apply {
                        setAttribute("action", step.action)
                        setAttribute("times", step.times.toString())
                        setAttribute("modifiers", step.modifiers.toString())
                    })
                }
            })
        }
    }

    fun deserializeFrom(element: Element?) {
        if (element == null) {
            mutableDemacros = defaultDemacros()
            return
        }

        val allNames = mutableSetOf<String>()
        mutableDemacros = element.children.map { d ->
            val name = d.getAttributeValue("name") ?: throw Exception("Missing demacro name")
            val enabled = d.getAttributeValue("enabled")?.toBoolean() ?: true

            if (!allNames.add(name)) throw Exception("Demacro name must be unique: \"$name\"")
            if (d.children == null || d.children.isEmpty()) throw Exception("Missing demacro steps")

            val steps = d.children.map { s ->
                val action = s.getAttributeValue("action") ?: throw Exception("Missing demacro step action")
                val times = s.getAttributeValue("times")?.toIntOrNull() ?: 1
                val modifiers = s.getAttributeValue("modifiers")?.toIntOrNull() ?: 0

                if (times <= 0) throw Exception("Invalid repeat times: $times")
                if (modifiers < 0) throw Exception("Invalid modifiers: $modifiers")

                Demacro.Step(action, times, modifiers)
            }

            Demacro(name, enabled, steps.toMutableList())
        }.toMutableList()
    }
}

private fun defaultDemacros() = mutableListOf<Demacro>(
    Demacro("Move Line Up by 5 Lines", true, mutableListOf(
        Demacro.Step(IdeActions.ACTION_EDITOR_MOVE_CARET_UP, 5)
    )),
    Demacro("Move Line Up by 5 Lines With Selection", true, mutableListOf(
        Demacro.Step(IdeActions.ACTION_EDITOR_MOVE_CARET_UP_WITH_SELECTION, 5)
    )),
    Demacro("Move Line Down by 5 Lines", true, mutableListOf(
        Demacro.Step(IdeActions.ACTION_EDITOR_MOVE_CARET_DOWN, 5)
    )),
    Demacro("Move Line Down by 5 Lines With Selection", true, mutableListOf(
        Demacro.Step(IdeActions.ACTION_EDITOR_MOVE_CARET_DOWN_WITH_SELECTION, 5)
    )),
    Demacro("Move Line Up by 10 Lines", true, mutableListOf(
        Demacro.Step(IdeActions.ACTION_EDITOR_MOVE_CARET_UP, 10)
    )),
    Demacro("Move Line Up by 10 Lines With Selection", true, mutableListOf(
        Demacro.Step(IdeActions.ACTION_EDITOR_MOVE_CARET_UP_WITH_SELECTION, 10)
    )),
    Demacro("Move Line Down by 10 Lines", true, mutableListOf(
        Demacro.Step(IdeActions.ACTION_EDITOR_MOVE_CARET_DOWN, 10)
    )),
    Demacro("Move Line Down by 10 Lines With Selection", true, mutableListOf(
        Demacro.Step(IdeActions.ACTION_EDITOR_MOVE_CARET_DOWN_WITH_SELECTION, 10)
    ))
)