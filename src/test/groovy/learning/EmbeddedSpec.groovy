package learning

import spock.lang.Shared
import spock.lang.Specification
import spock.util.EmbeddedSpecCompiler
import spock.util.EmbeddedSpecRunner

abstract class EmbeddedSpec extends Specification {

    @Shared
    EmbeddedSpecCompiler compiler = new EmbeddedSpecCompiler()

    @Shared
    EmbeddedSpecRunner runner = new EmbeddedSpecRunner()

}