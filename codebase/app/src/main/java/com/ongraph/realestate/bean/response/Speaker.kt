package com.ongraph.realestate.bean.response

data class Speaker(val id:String,
                   val profilePic:String,
                   val profilePicThumb:String,
                   val firstName:String,
                   val lastName:String,
                   val email:String,
                   val userType:String,
                   val role:String,
                   val description:String) {

    override fun toString(): String {
        return "Speaker(id='$id', profilePic='$profilePic', profilePicThumb='$profilePicThumb', firstName='$firstName', lastName='$lastName', email='$email', userType='$userType', role='$role', description='$description')"
    }
}


