#include <string.h>
#include <jni.h>

jstring
Java_com_moczul_renderscript_MainActivity_getProcessorType(JNIEnv* env, jobject thiz)
{
#if defined(__arm__)
    #if defined(__ARM_ARCH_7A__)
        #define ABI "armeabi-v7a"
    #else
        #define ABI "armeabi"
    #endif
#elif defined(__i386__)
    #define ABI "x86"
#elif defined(__mips__)
    #define ABI "mips"
#else
    #define ABI "unknown"
#endif

    return (*env)->NewStringUTF(env, ABI);
}