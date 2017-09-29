import React, {Component} from 'react';
import '../app/App.css';
import '../../node_modules/react-bootstrap-table/dist/react-bootstrap-table-all.min.css';
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table';
import {Button} from "react-bootstrap";
import {
    cellEditProp,
    selectRowProp,
    updateButtonClick,
    onAddRow,
    onCellEdit,
    onDeleteRow,
    noButtonClick
} from '../helper/TableHelper'

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

    render() {
        let tables = [];
        for (let i in this.state.data) {
            let result = this.state.data[i];
            if (result.content.length === 0) break;
            let props = Object.keys(result.content[0]);
            let tableData = [];
            for (let j in props) {
                let propertyName = props[j];
                if (propertyName === "hash") {
                    tableData.push(<TableHeaderColumn searchable={false} isKey hidden hiddenOnInsert export={false}
                                                      key={propertyName}
                                                      dataField={propertyName}>{propertyName}</TableHeaderColumn>)
                } else {
                    tableData.push(<TableHeaderColumn searchable={true} key={propertyName}
                                                      dataField={propertyName}>{propertyName}</TableHeaderColumn>)
                }
            }

            const options = {
                clearSearch: true,
                onCellEdit: onCellEdit.bind(this, result.source),
                onAddRow: onAddRow.bind(this, result.source),
                onDeleteRow: onDeleteRow.bind(this, result.source)
            };

            let table;
            let changedForm;
            if (this.state.changed[this.state.data.indexOf(result)] === true) {
                changedForm =
                    <div>
                        <h3>We detected that these rows changed in your file. Would you like to update them?</h3>
                        <Button onClick={updateButtonClick.bind(this)} bsStyle="primary">Update</Button>
                        <Button onClick={noButtonClick.bind(this)}>No</Button>
                    </div>;
                table = <BootstrapTable replace={true}
                                        data={result.content}
                                        striped hover>
                    {tableData}
                </BootstrapTable>;
            } else if (this.state.data.length !== 0) {
                table = <BootstrapTable replace={true}
                                        data={result.content}
                                        cellEdit={cellEditProp}
                                        selectRow={selectRowProp}
                                        csvFileName={this.state.csvFileName}
                                        options={options}
                                        remote={true}
                                        insertRow={true}
                                        deleteRow={true}
                                        search
                                        exportCSV={true}
                                        striped hover>
                    {tableData}
                </BootstrapTable>;
            }

            tables.push(
                <div>
                    <h2>{result.source}</h2>
                    {changedForm}
                    <br/>
                    {table}
                </div>);
        }

        return (
            <div>
                <h1>{this.state.filename}</h1>
                {tables}
            </div>
        );
    }
}

export default Table;