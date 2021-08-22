package com.vincent.forexledger.model.user

import com.google.firebase.auth.UserInfo

enum class SocialLoginProvider(val providerId: String) {
    FACEBOOK("facebook.com"),
    GOOGLE("google.com");

    companion object {
        fun fromProviderId(userInfoList: List<UserInfo>): SocialLoginProvider? {
            val info = userInfoList.firstOrNull { it.providerId != "firebase" }
            return values().firstOrNull { it.providerId == info?.providerId }
        }
    }
}