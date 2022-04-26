package hello.friend

class UserData {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var imageUrl: String? = null

    constructor() {}

    constructor(name: String?, email: String?, uid: String?, imageUrl: String?) {
        this.name = name
        this.email = email
        this.uid = uid
        this.imageUrl = imageUrl

    }
}
