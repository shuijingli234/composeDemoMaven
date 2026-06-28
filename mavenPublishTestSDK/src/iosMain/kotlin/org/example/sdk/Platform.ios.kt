package org.example.sdk

import platform.UIKit.UIDevice

actual fun getPlatformName(): String = "iOS ${UIDevice.currentDevice.systemVersion}"
