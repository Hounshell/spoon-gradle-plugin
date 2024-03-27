package fr.inria.gforge.spoon

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import spoon.Launcher
import spoon.OutputType
import spoon.compiler.Environment
import spoon.processing.Processor

class SpoonAndroidTask extends DefaultTask {
    @InputDirectory
    FileCollection srcFolders

    @InputFiles
    FileCollection srcPath

    @OutputDirectory
    File outFolder

    @Input
    boolean preserveFormatting

    @Input
    boolean noClasspath

    @Input
    String[] processors = []

    @Input
    Processor[] processorsInstance = []

    @Classpath
    FileCollection classpath

    @Input
    int compliance

    void init() {
        def log = project.logger
        printEnvironment(log.&debug)
        if (project.spoon.debug) {
            printEnvironment(System.out.&println)
        }

    }

    void configureEnvironment(Environment environment) {
        environment.setComplianceLevel(compliance)
        environment.setNoClasspath(noClasspath)
        environment.setPreserveLineNumbers(preserveFormatting)
        environment.setSourceOutputDirectory(outFolder)
        environment.setSourceClasspath(classpath.asPath.split(File.pathSeparator))
        environment.setOutputType(OutputType.COMPILATION_UNITS)
    }

    @TaskAction
    void run() {
        init()
        // No source code to spoon.
        if (srcFolders.size() == 0) {
            project.logger.debug("No source file defined.")
            return
        }

        Launcher launcher = new Launcher()
        configureEnvironment(launcher.getEnvironment())

        srcFolders.files.each { directory -> launcher.addInputResource(directory.getPath()) }
        srcPath.files.each { directory -> launcher.addInputResource(directory.getPath()) }


        processors.collect {
            it -> launcher.addProcessor(it)
        }
        processorsInstance.collect {
            it -> launcher.addProcessor(it)
        }

        launcher.run();
    }

    def printEnvironment(printer) {
        printer "----------------------------------------"
        printer "source folder: $srcFolders.asPath"
        printer "output folder: $outFolder"
        printer "src path: $srcPath.asPath"
        printer "preserving formatting: $preserveFormatting"
        printer "no classpath: $noClasspath"
        printer "processors: $processors"
        printer "classpath: $classpath.asPath"
        printer "----------------------------------------"
    }
}
