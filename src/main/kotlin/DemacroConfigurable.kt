import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.util.JDOMUtil
import org.jdom.Element
import org.jdom.input.SAXBuilder
import org.jdom.output.XMLOutputter
import java.io.StringReader
import javax.swing.JComponent

class DemacroConfigurable : SearchableConfigurable {
    private val form: DemacroConfigurableForm by lazy {
        DemacroConfigurableForm()
    }

    private var previousText: String? = null

    override fun getDisplayName() = "Demacro Configuration"
    override fun getHelpTopic() = "preferences.Demacro"
    override fun getId() = "preferences.Demacro"

    override fun createComponent(): JComponent? {
        fun setEditorText(text: String) {
            ApplicationManager.getApplication().runWriteAction {
                form.editor.document.setText(text)
            }
        }

        form.restoreDefaultsButton.addActionListener {
            setEditorText(DemacroStore().apply {
                deserializeFrom(null)
            }.xmlRepresentation())
        }

        form.editor.document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                form.errorLabel.text = null
            }
        })

        previousText = DemacroPlugin.shared.store.xmlRepresentation().apply {
            setEditorText(this)
        }

        return form.rootPanel
    }

    override fun isModified() = form.editor.document.text != previousText

    override fun apply() {
        if (form.editor.document.textLength == 0) {
            DemacroPlugin.shared.store = DemacroStore()
            previousText = ""
            form.errorLabel.text = null
        } else {
            try {
                SAXBuilder().build(StringReader(form.editor.document.text)).rootElement?.let {
                    DemacroPlugin.shared.store = DemacroStore().apply { deserializeFrom(it) }
                } ?: throw Exception("Failed to parse XML")

                previousText = form.editor.document.text
                form.errorLabel.text = null
            } catch (e: Exception) {
                form.errorLabel.text = "<html>Invalid configuration:<br/>$e</html>"
            }
        }
    }
}

private val log = Logger.getInstance(DemacroConfigurable::class.java)

private fun DemacroStore.xmlRepresentation() : String {
    return Element("demacros").let {
        serializeInto(it)
        XMLOutputter(JDOMUtil.createFormat("\n")).outputString(it)
    }
}