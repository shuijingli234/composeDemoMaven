package org.example.sdk

class SimpleSdk {
    fun hello(): String = "Hello from SimpleSdk!"

    fun add(a: Int, b: Int): Int = a + b
}

fun getSdkVersion(): String = "1.0.0-fork"
