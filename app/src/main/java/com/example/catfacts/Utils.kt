package com.example.catfacts

class Utils {
    companion object {
        val TIP_REGEX_PATTERN = """^\d{1,2}(\.\d+)?%$""".toRegex()
    }
}