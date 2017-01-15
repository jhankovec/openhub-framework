import tc from 'tinycolor2'
import { primaryColor, secondaryColor, darkColor } from '../../styles/colors'
import { itemSize } from '../../styles/constants'

export default {
  main: {
    position: 'relative',
    width: '100%',
    height: `${itemSize}px`,
    lineHeight: `${itemSize}px`,
    backgroundColor: primaryColor
  },
  menuIcon: {
    cursor: 'pointer',
    color: secondaryColor
  },
  left: {
    float: 'left',
    textAlign: 'center',
    height: `${itemSize}px`,
    maxWidth: '50%',
    minWidth: `${itemSize}px`
  },
  right: {
    float: 'right',
    height: `${itemSize}px`,
    lineHeight: `${itemSize}px`,
    maxWidth: '50%',
    minWidth: `${itemSize}px`
  },
  item: {
    backgroundColor: 'transparent',
    color: tc(darkColor).lighten(30).toString(),
    ':hover': {
      backgroundColor: tc(secondaryColor).setAlpha(0.2).toString(),
      color: darkColor
    }
  }
}
