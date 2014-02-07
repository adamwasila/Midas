package com.ee.midas

import com.ee.midas.transform.TransformType
import spock.lang.Shared
import spock.lang.Specification

class ConfigurationSpecs extends Specification {

    @Shared File configDir

    def setupSpec() {
        def configDirname = "midasConfig"
        configDir = new File(configDirname)
        configDir.mkdir()
    }

    def cleanupSpec() {
        configDir.deleteDir()
    }

    private def createConfiguration(String contents) {
        def config = new File("$configDir/midas.config")
        config.withWriter { writer ->
            writer.write(contents)
        }
        config.toURI().toURL()
    }

    def "creates configuration using URL"() {
        given: 'a configuration converted to a file resource'
            def config = """
                             apps {
                                mode = 'contraction'
                             }
                         """.stripMargin()

            def configURL = createConfiguration(config)

        when: 'configuration is read in'
            def configuration = new Configuration(configURL)

        then: 'it should return the mode set in configuration file'
           configuration.mode() == TransformType.CONTRACTION
    }

}