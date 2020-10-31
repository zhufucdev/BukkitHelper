package com.zhufucdev.bukkithelper.impl

import com.zhufucdev.bukkit_helper.Plugin
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarInputStream

/**
 * Represent a plugin to be loaded in a jar file.
 */
class JarPlugin(val file: File) : AbstractPlugin() {
    override val name: String
    override val description: String
    private val pluginType: Class<Plugin>

    init {
        val jar = JarInputStream(file.inputStream())
        val loader = URLClassLoader(arrayOf(URL("jar", "", "file:" + file.toURI().toASCIIString())))
        // Look for manifest
        val manifest: Map<String, Any>
        kotlin.run {
            var mf: Map<String, Any>? = null
            var entry = jar.nextEntry
            while (entry != null) {
                if (entry.name == MANIFEST_NAME) {
                    mf = Yaml().load(jar)
                    break
                }
                entry = jar.nextEntry
            }

            manifest = mf ?: error("${file.absolutePath} doesn't contain a $MANIFEST_NAME.")
        }
        val main = manifest["main"] as String? ?: error("${file.absolutePath} doesn't have a property of main in $MANIFEST_NAME.")
        pluginType = try {
            loader.loadClass(main) as Class<Plugin>
        } catch (e: ClassCastException) {
            error("${file.absolutePath}/$main isn't of type ${Plugin::class.qualifiedName}.")
        } catch (e: ClassNotFoundException) {
            error("${file.absolutePath} doesn't have main class of $main.")
        }
        name = manifest["name"] as String? ?: error("${file.absolutePath} doesn't have a property of name in $MANIFEST_NAME.")
        description = manifest["description"] as String? ?: error("${file.absolutePath} doesn't have a property " +
                "of description in $MANIFEST_NAME")
    }

    override fun mLoad() {
        mInstance = pluginType.newInstance()
    }

    override fun mUnload() {
        mInstance?.onDisable()
        mInstance = null
    }

    companion object {
        const val MANIFEST_NAME = "plugin.yml"
    }
}