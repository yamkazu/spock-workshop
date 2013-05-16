package learning.basic

import learning.EmbeddedSpec

/**
 * {@code cleanup:}ブロックの使い方
 */
class CleanupSpec extends EmbeddedSpec {

    def "cleanupの使い方"() {
        when:
        def file = File.createTempFile("spock", ".txt")

        then:
        notThrown(IOException)

        cleanup: "ここでお掃除"
        file.delete()
    }
}
