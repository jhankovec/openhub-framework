import React, { PropTypes, Component } from 'react'
import Radium from 'radium'
import FontIcon from 'react-md/lib/FontIcons'
import TableRow from 'react-md/lib/DataTables/TableRow'
import Button from 'react-md/lib/Buttons/Button'
import TableColumn from 'react-md/lib/DataTables/TableColumn'
import { pipe, omit, values, map, append, addIndex, toString } from 'ramda'

@Radium
class ParamRow extends Component {

  render () {
    const { data, openParam } = this.props
    const id = data.id

    const ensureNumberOfCells = {
      code: '',
      currentValue: '',
      defaultValue: '',
      dataType: '',
      mandatory: '',
      description: '',
      validationRegEx: ''
    }

    const formatType = (value) => {
      if (typeof value === 'boolean') {
        return value
          ? <FontIcon style={{ marginTop: '5px', fontSize: '20px' }}>check_box</FontIcon>
          : <FontIcon style={{ marginTop: '5px', fontSize: '20px' }}>check_box_outline_blank</FontIcon>
      }
      return typeof value !== 'string' ? toString(value) : value
    }

    const cells = pipe(
      omit(['id', 'categoryCode', 'dataType', 'mandatory', 'validationRegEx']),
      values,
      map(formatType),
      addIndex(map)((cell, index) => <TableColumn key={index} >{cell}</TableColumn>),
      append(
        <TableColumn key={id} >
          <Button primary onClick={() => openParam(id)} >edit</Button >
        </TableColumn>)
    )({ ...ensureNumberOfCells, ...data })

    return (
      <TableRow >
        {cells}
      </TableRow>
    )
  }
}

ParamRow.propTypes = {
  data: PropTypes.object,
  openParam: PropTypes.func,
  count: PropTypes.number
}

export default ParamRow
