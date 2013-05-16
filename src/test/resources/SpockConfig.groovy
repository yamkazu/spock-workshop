import learning.extension.Fast
import learning.extension.IncludeExcludeSpec
import learning.extension.Slow
import learning.extension.Server

// Firstが付与されているものを実行
//runner {
//    include Fast
//}

//Slowが付与さているものだけ除外
runner {
    exclude Slow
}

// Serverが付与されているものの中でFastとSlowを除外
//runner {
//    include Server
//    exclude Fast, Slow
//}

// スペッククラスを指定することも可能(ベースでもOK)
//runner {
//    include IncludeExcludeSpec
//}
