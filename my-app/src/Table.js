import React, {Component} from 'react';
import './App.css';
import '../node_modules/react-bootstrap-table/dist/react-bootstrap-table-all.min.css';
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table';
import {Button} from "react-bootstrap";

class Table extends Component {

    constructor(props) {
        super(props);
        this.state = {
            data: props.data,
            changed: props.changed,
            filename: props.filename
        }
    }

    updateButtonClick(e) {
        console.log(this.state.data);
        fetch('http://localhost:8080/api/fileload/' + this.state.filename + '/update', {
            method: 'POST',
            body: {values:this.state.data},
        }).then(function (response) {
            return response.json()
        }).then(function (json) {
            temp.setState({
                data: json.values,
                changed: json.changed,
                filename: this.state.filename,
                showTable: true
            });
        }).catch(function (ex) {
            console.log('parsing failed', ex)
        });
    }

    render() {
        /*function beforeSaveCell(row, cellName, cellValue) {
            // if you dont want to save this editing, just return false to cancel it.
            alert("db update");
        }

        function onAddRow(row) {
            console.log(row);
        }

        function onDeleteRow(rows, e) {
            console.log(rows);
            console.log(e)
        }*/

        const options = {
            clearSearch: true,
            /*onAddRow: onAddRow,
            onDeleteRow: onDeleteRow*/
        };

        const cellEditProp = {
            //beforeSaveCell: beforeSaveCell,
            mode: 'click'
        };

        const selectRowProp = {
            mode: 'checkbox'
        };


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
                tableData.push(<TableHeaderColumn isKey hidden hiddenOnInsert export={false} key={propertyName}
                                                  dataField={propertyName}>{propertyName}</TableHeaderColumn>)
            } else {
                tableData.push(<TableHeaderColumn key={propertyName}
                                                  dataField={propertyName}>{propertyName}</TableHeaderColumn>)
            }
        }

        let table = undefined;
        if (tableData.length !== 0) {
            table = <BootstrapTable
                data={this.state.data}
                remote={true}
                cellEdit={cellEditProp}
                insertRow={true}
                deleteRow={true}
                search={true}
                exportCSV={true}
                options={options}
                selectRow={selectRowProp}
                striped hover>
                {tableData}
            </BootstrapTable>;
        }

        let changedForm;
        if (this.state.changed === true) {
            changedForm =
                <div>
                    <h3>We detected that these rows changed in your file. Would you like to update them?</h3>
                    <Button onClick={this.updateButtonClick.bind(this)} bsStyle="primary">Update</Button>
                </div>
        }

        return (
            <div>
                <h2>{this.state.filename}</h2>
                {table}
                {changedForm}
            </div>
        );
    }
}

const temp = [
    {
        "first_name": "Michiel",
        "last_name": "Huh",
        "email": "schiommienti0@china.com.cn",
        "gender": "Female",
        "ip_address": "98.2.23.154",
        "date": 1506463200000,
        "doubles": 0.19916165334139824,
        "hash": -2081097150
    },
    {
        "first_name": "Dockx",
        "last_name": "What",
        "email": "ilasham1@cocolog-nifty.com",
        "gender": "Female",
        "ip_address": "113.214.208.7",
        "date": 1506549600000,
        "doubles": 0.4163163720275469,
        "hash": 952641049
    }
];

export default Table;