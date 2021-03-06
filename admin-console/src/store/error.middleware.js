import { path } from 'ramda'
import { toastr } from 'react-redux-toastr'
import { logoutUser, toggleLoginModal } from '../common/modules/auth.module'
import { FETCH_ERROR } from '../services/api.module'

export default (store) => (next) => (action) => {
  if (action.type === FETCH_ERROR) {
    const errorResponse = path(['payload', 'response', 'response'], action)
    const status = errorResponse.status
    const errorMessage = path(['payload', 'response', 'message'], action)
    const errorCode = path(['payload', 'response', 'errorCode'], action)

    if (status === 401) {
      store.dispatch(logoutUser())
      store.dispatch(toggleLoginModal())
      return
    }

    if (status === 404) {
      toastr.error(`404 Error`, 'Not Found')
    }

    if (errorCode && errorMessage) {
      toastr.error(`Error ${errorCode}`, errorMessage)
    }
  }
  next(action)
}
