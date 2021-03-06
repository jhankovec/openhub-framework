import React from 'react'
import ReactDOM from 'react-dom'
import ApiService from './services/api.service'
import WebFontLoader from 'webfontloader'
import createStore from './store/createStore'
import AppContainer from './common/containers/app.container'
import 'whatwg-fetch'
import './styles.scss'

// ========================================================
// Fonts Instantiation
// ========================================================
WebFontLoader.load({
  custom: {
    families: ['Roboto:300,400,500,700', 'Material Icons'],
    urls: ['offline/fonts/index.css']
  }
})

// ========================================================
// Store Instantiation
// ========================================================
const initialState = window.___INITIAL_STATE__
const store = createStore(initialState)

// ========================================================
// API Instantiation
// ========================================================
ApiService.connectApiToStore(store)

// ========================================================
// Render Setup
// ========================================================
const MOUNT_NODE = document.getElementById('root')

let render = () => {
  const routes = require('./routes/index').default(store)

  ReactDOM.render(
    <AppContainer store={store} routes={routes} />,
    MOUNT_NODE
  )
}

// This code is excluded from production bundle
if (__DEV__) {
  if (module.hot) {
    // Development render functions
    const renderApp = render
    const renderError = (error) => {
      const RedBox = require('redbox-react').default

      ReactDOM.render(<RedBox error={error} />, MOUNT_NODE)
    }

    // Wrap render in try/catch
    render = () => {
      try {
        renderApp()
      } catch (error) {
        renderError(error)
      }
    }

    // Setup hot module replacement
    module.hot.accept('./routes/index', () =>
      setImmediate(() => {
        ReactDOM.unmountComponentAtNode(MOUNT_NODE)
        render()
      })
    )
  }
}

// ========================================================
// Go!
// ========================================================
render()
