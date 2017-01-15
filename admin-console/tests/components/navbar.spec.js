const { describe, it } = global
import React from 'react'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { spy } from 'sinon'
import Navbar from '../../src/components/Navbar/Navbar'
import MenuIcon from 'react-icons/lib/md/menu'
import UserButton from '../../src/components/UserButton/UserButton'

const getWrapper = (props, context) => {
  return shallow(
    <Navbar{...props} />, { context })
}

describe('Navbar Component', () => {
  const toggleSidebar = spy()

  const wrapper = getWrapper(
    {
      toggleSidebar
    }
  )

  it('should render', () => {
    expect(wrapper.find('.navbar-wrapper')).to.have.length(1)
    expect(wrapper.find('.navbar-wrapper')).to.have.descendants(MenuIcon)
    expect(wrapper.find('.navbar-wrapper')).to.have.descendants(UserButton)
  })

  it('should trigger sidebar open', () => {
    expect(toggleSidebar).to.have.property('callCount', 0)
    wrapper.find('.sidebar-toggle').simulate('click')
    expect(toggleSidebar).to.have.property('callCount', 1)
  })
})
