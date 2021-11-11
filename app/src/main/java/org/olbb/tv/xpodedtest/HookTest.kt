package org.olbb.tv.xpodedtest

import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HookTest: IXposedHookLoadPackage{

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        XposedBridge.log("handleLoadPackage:${lpparam?.packageName}")
        lpparam?.takeIf { "org.olbb.tv.test360" == it.packageName }?.let { param ->
            XposedBridge.log("has hooked!")
            try {
                XposedHelpers.findAndHookMethod("com.stub.StubApp", param.classLoader,
                    "attachBaseContext", Context::class.java, object : XC_MethodHook(){
                        override fun afterHookedMethod(param: MethodHookParam?) {
                            XposedBridge.log("afterHookedMethod attachBaseContext")
                            super.afterHookedMethod(param)
                            val context = param?.args?.get(0) as Context
                            val classLoader = context.classLoader
                            realHook(classLoader)
                        }
                    })

            } catch (e : Exception) {
                e.printStackTrace()
            }


        }
    }

    private fun realHook(classLoader: ClassLoader) {
        val clazz = classLoader.loadClass("org.olbb.tv.test360.MainActivity")
        XposedHelpers.findAndHookMethod(clazz, "getTag", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                super.beforeHookedMethod(param)
                XposedBridge.log("beforeHookedMethod:$param")
            }

            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                param?.result = "Faked."

            }
        })
    }
}