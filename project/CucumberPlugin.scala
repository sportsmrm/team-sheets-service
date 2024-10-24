import sbt.Keys.{Classpath, fullClasspath, thisProject}
import sbt.OutputStrategy.StdoutOutput
import sbt.PluginTrigger.NoTrigger
import sbt.librarymanagement.Configurations.{Runtime, Test}
import sbt.plugins.JvmPlugin
import sbt.{AutoPlugin, Def, Fork, ForkOptions, MessageOnlyException, inConfig, settingKey, taskKey}


object CucumberPlugin extends AutoPlugin {
  override def requires = JvmPlugin
  override def trigger = NoTrigger
  object autoImport {
    lazy val cucumber = taskKey[Unit]("Run cucumber")
    lazy val cucumberScenariosDir = settingKey[Seq[String]]("The scenarios to run. Defaults to those in the 'features' directory.")
  }

  import autoImport.*
  lazy val baseCucumberSettings: Seq[Def.Setting[_]] = Seq(
    cucumber := Cucumber((Test / fullClasspath).value, cucumberScenariosDir.value),
    cucumberScenariosDir := Seq((thisProject).value.base + "/features")
  )
  override lazy val projectSettings = inConfig(Runtime)(baseCucumberSettings)
}

object Cucumber {
  def apply(fullClasspath: Classpath, scenarios: Seq[String]): Unit = {
    val forkOptions = ForkOptions()
      .withOutputStrategy(Some(StdoutOutput))
      .withConnectInput(false)
      .withRunJVMOptions(Vector(
        "-classpath", fullClasspath.files mkString ":",
        "io.cucumber.core.cli.Main"
      ))

    val result = Fork.java(forkOptions, scenarios)
    if (result != 0) {
      throw new MessageOnlyException(s"Non-zero exit code: $result")
    }
  }
}
