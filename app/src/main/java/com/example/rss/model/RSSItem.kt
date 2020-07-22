package com.example.rss.model

class RSSItem {
    var title = ""
    var link = ""
    var imgLink = ""
    var description = ""

    override fun toString(): String {
        return "RSSItem"+
                "\nTitle: " + title +
                "\nLink: " + link +
                "\nImgLink: " + imgLink +
                "\nDescription: " + description;
    }
}