package com.yelinaung.firebaselogin

import kotlin.properties.Delegates

/**
 * Created by user on 7/19/16.
 */

class User(var ogName: String) {

    var name: String by Delegates.observable("<no name>") { prop, old, new ->
        println("$old -> $new")

    }

    var us: User by Delegates.observable(User("")){ prop, old, new ->

    }

}