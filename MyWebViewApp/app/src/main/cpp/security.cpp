#include <jni.h>
#include <string>
#include <random>
#include <chrono>
#include <sstream>
#include <sys/ptrace.h>

// Anti-debug and root detection
extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_mywebviewapp_SecurityUtils_checkSecurity(JNIEnv *env, jobject /* this */) {
    // Check if debugger is attached
    if (ptrace(PTRACE_TRACEME, 0, 1, 0) == -1) {
        return JNI_FALSE;
    }

    // Time-based anti-debug check
    auto start = std::chrono::high_resolution_clock::now();
    volatile int counter = 0;
    for(int i = 0; i < 1000; i++) counter++;
    auto end = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(end - start);
    
    if(duration.count() > 10000) { // If debugger is present, this will take longer
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

// Native string obfuscation
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_mywebviewapp_SecurityUtils_getObfuscatedString(JNIEnv *env, jobject /* this */, jstring key) {
    const char *keyStr = env->GetStringUTFChars(key, 0);
    std::string result;
    
    // Complex string transformation
    for(int i = 0; keyStr[i] != '\0'; i++) {
        char c = keyStr[i];
        c = (c << 4) | (c >> 4); // Bit rotation
        c ^= 0xAA; // XOR transformation
        result += c;
    }
    
    env->ReleaseStringUTFChars(key, keyStr);
    return env->NewStringUTF(result.c_str());
}