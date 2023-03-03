/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * Learn more about Gradle by exploring our samples at https://docs.gradle.org/8.0.1/samples
 * This project uses @Incubating APIs which are subject to change.
 */

val localSpecPath = providers.gradleProperty("localSpecPath")
val remoteSpecUrl = providers.gradleProperty("remoteSpecUrl").orElse(
  "https://docs.gradle.com/enterprise/api-manual/ref/gradle-enterprise-2022.4-api.yaml"
)

val downloadRemoteSpec by tasks.registering {
  onlyIf { !localSpecPath.isPresent() }
  val spec = resources.text.fromUri(remoteSpecUrl)
  val specName = remoteSpecUrl.map { it.substringAfterLast('/') }
  val outFile = project.layout.buildDirectory.file(specName)
  inputs.property("Remote URL", remoteSpecUrl)
  outputs.file(outFile)
  doLast {
      spec.asFile().renameTo(outFile.get().asFile)
  }
}

// Infers task depends on downloadRemoteSpec task as expected
val displaySpecOk by tasks.registering {
  val path = downloadRemoteSpec.map { it.outputs.files.first().absolutePath }
  inputs.property("Spec path", path)
  doLast {
    logger.quiet(File(path.get()).readText())
  }
}

// Expected: If localSpecPath isn't present, task depends on downloadRemoteSpec
// Actual: If localSpecPath isn't present, task is run without downloadRemoteSpec
val displaySpecNotOk by tasks.registering {
  val path = localSpecPath.map { File(it).absolutePath }
    .orElse(downloadRemoteSpec.map { it.outputs.files.first().absolutePath })
  inputs.property("Spec path", path)
  doLast {
    logger.quiet(File(path.get()).readText())
  }
}

val clean by tasks.registering(Delete::class) {
  delete(project.layout.buildDirectory)
}
