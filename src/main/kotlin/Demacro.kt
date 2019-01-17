data class Demacro(var name: String, var isEnabled: Boolean = true, var steps: MutableList<Step>) {
    data class Step(var action: String, var times: Int, var modifiers: Int = 0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Demacro

        if (name != other.name) return false
        if (isEnabled != other.isEnabled) return false
        if (steps != other.steps) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + isEnabled.hashCode()
        result = 31 * result + steps.hashCode()
        return result
    }
}