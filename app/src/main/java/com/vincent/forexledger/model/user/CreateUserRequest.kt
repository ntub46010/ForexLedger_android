package com.vincent.forexledger.model.user

data class CreateUserRequest(
        var email: String,
        var name: String,
        var socialLoginProvider: SocialLoginProvider,
        var firebaseUid: String)
