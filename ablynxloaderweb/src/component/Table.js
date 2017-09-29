import React, {Component} from 'react';
import '../app/App.css';
import '../../node_modules/react-bootstrap-table/dist/react-bootstrap-table-all.min.css';
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table';
import {Button} from "react-bootstrap";
import {cellEditProp, selectRowProp, updateButtonClick, onAddRow, onCellEdit, onDeleteRow, noButtonClick} from '../helper/TableHelper'

class Table extends Component {

    constructor(props) {
        super(props);
        this.state = {
            data: props.data,
            changed: props.changed,
            filename: props.filename,
            csvFileName: props.filename.split('.')[0] + ".csv"
        }
    }

    options = {
        clearSearch: true,
        onCellEdit: onCellEdit.bind(this),
        onAddRow: onAddRow.bind(this),
        onDeleteRow: onDeleteRow.bind(this)
    };

    render() {
        let mostProps;
        let count = 0;
        for (let i = 0; i < this.state.data.length; i++) {
            let temp = Object.keys(this.state.data[i]).length;
            if (temp > count) {
                count = temp;
                mostProps = this.state.data[i];
            }
        }

        let tableData = [];
        for (let propertyName in mostProps) {
            if (propertyName === "hash") {
                tableData.push(<TableHeaderColumn searchable={false} isKey hidden hiddenOnInsert export={false} key={propertyName}
                                                  dataField={propertyName}>{propertyName}</TableHeaderColumn>)
            } else {
                tableData.push(<TableHeaderColumn searchable={true} key={propertyName}
                                                  dataField={propertyName}>{propertyName}</TableHeaderColumn>)
            }
        }

        let table;
        let changedForm;
        if (this.state.changed === true) {
            changedForm =
                <div>
                    <h3>We detected that these rows changed in your file. Would you like to update them?</h3>
                    <Button onClick={updateButtonClick.bind(this)} bsStyle="primary">Update</Button>
                    <Button onClick={noButtonClick.bind(this)}>No</Button>
                </div>;
            table = <BootstrapTable replace={true}
                data={this.state.data}
                striped hover>
                {tableData}
            </BootstrapTable>;
        } else if (this.state.data.length !== 0) {
            table = <BootstrapTable replace={true}
                data={this.state.data}
                cellEdit={cellEditProp}
                selectRow={selectRowProp}
                csvFileName={this.state.csvFileName}
                options={this.options}
                remote={true}
                insertRow={true}
                deleteRow={true}
                search
                exportCSV={true}
                striped hover>
                {tableData}
            </BootstrapTable>;
        }

        return (
            <div>
                <h2>{this.state.filename}</h2>
                {changedForm}
                <br/>
                {table}
            </div>
        );
    }
}

export default Table;