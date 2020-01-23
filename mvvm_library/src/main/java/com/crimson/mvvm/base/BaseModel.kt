package com.crimson.mvvm.base

import com.crimson.mvvm.net.RemoteService
import org.koin.core.inject


/**
 * @author crimson
 * @date   2019-12-21
 * base model
 */
open class BaseModel : IModel {

    val remoteService by inject<RemoteService>()


}