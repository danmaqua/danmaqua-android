package moe.feng.danmaqua.util.ext

import moe.feng.danmaqua.model.LicenseItem

class LicenseItemBuilder {

    var name: String = ""
    var license: String = ""
    var author: String = ""
    var url: String = ""

    fun githubUrl(repoPath: String) {
        url = "https://github.com/$repoPath"
    }

    fun build(): LicenseItem {
        return LicenseItem(name, license, author, url)
    }

}

class LicensesListBuilder {

    private val list = mutableListOf<LicenseItem>()

    fun license(block: LicenseItemBuilder.() -> Unit) {
        list += LicenseItemBuilder().apply(block).build()
    }

    fun sortByName() {
        list.sortBy { it.productName }
    }

    fun build() = list.toList()

}

fun licenses(block: LicensesListBuilder.() -> Unit): List<LicenseItem> {
    return LicensesListBuilder().apply(block).build()
}

fun LicensesListBuilder.apache2(block: LicenseItemBuilder.() -> Unit) {
    license {
        license = "Apache 2.0 License"
        block()
    }
}

fun LicensesListBuilder.mit(block: LicenseItemBuilder.() -> Unit) {
    license {
        license = "MIT License"
        block()
    }
}

fun LicensesListBuilder.bsd3clause(block: LicenseItemBuilder.() -> Unit) {
    license {
        license = "BSD 3-Clause License"
        block()
    }
}
