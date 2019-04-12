package com.novoda.movies.gallery

import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.px
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import react.setState
import styled.css
import styled.styledDiv
import styled.styledImg

interface AppState : RState {
    var gallery: Gallery?
    var message: String?
}

private class App : RComponent<RProps, AppState>(), GalleryPresenter.View {
    override fun render(gallery: Gallery) {
        setState {
            this.gallery = gallery
            message = null
        }
    }

    override fun renderError(message: String?) {
        setState {
            this.message = message
        }
    }

    val presenter = GalleryDependencyProvider().providerPresenter()

    override fun componentWillMount() {
        presenter.startPresenting(this)
    }

    override fun componentWillUnmount() {
        presenter.stopPresenting()
    }

    override fun RBuilder.render() {
        header(classes = "mdc-top-app-bar mdc-top-app-bar--fixed") {
            div(classes = "mdc-top-app-bar__row") {
                section(classes = "mdc-top-app-bar__section mdc-top-app-bar__section--align-start") {
                    span(classes = "mdc-top-app-bar__title") { +"Movies" }
                }
            }
        }
        state.gallery?.let {
            styledDiv {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.row
                }
            }
            it.moviePosters.forEach { poster ->
                styledImg(src = poster.thumbnailUrl) {
                    css {
                        width = 200.px
                    }
                }
            }
        }
        state.message?.let { h2 { +it } }
    }
}

fun RBuilder.app() = child(App::class) {
}
