package io.sportsmrm.util.config

import com.typesafe.config.{ConfigResolver, ConfigValue, ConfigValueFactory}

import scala.util.{Failure, Success, Using}

object DockerSecretConfigResolver {
  def apply(): DockerSecretConfigResolver = new DockerSecretConfigResolver(None)
}
class DockerSecretConfigResolver(private val fallback: Option[ConfigResolver] = None) extends ConfigResolver {

  private def readFile(fileName: String): Option[ConfigValue] = {
    Using(scala.io.Source.fromFile(fileName)) {source =>
      ConfigValueFactory.fromAnyRef(source.mkString, fileName)
    } match {
      case Success(value) => Some(value)
      case Failure(exception) => {
        None
      }
    }
  }

  private def getFromFile(path: String): Option[ConfigValue] = {
    val filePath = path + "_FILE"
    val fileName = System.getenv(filePath)
    if (fileName != null) {
      readFile(fileName)
    }
    else
      None
  }
  override def lookup(path: String): ConfigValue | Null = {
    getFromFile(path).getOrElse(fallback match
      case Some(fb) => fb.lookup(path)
      case None => null
    )
  }

  override def withFallback(fallback: ConfigResolver): ConfigResolver =
    this.fallback match {
      case Some(fb) if fb == fallback => this
      case _ => new DockerSecretConfigResolver(Some(fallback))
    }
}
