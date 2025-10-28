cmake_minimum_required(VERSION 3.4.1)

project(RiyaDSANative)

add_library(security SHARED
            security.cpp)

target_link_libraries(security
                     android
                     log)