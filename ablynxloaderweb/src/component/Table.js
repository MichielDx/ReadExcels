import React, {Component} from 'react';
import '../app/App.css';
import '../../node_modules/react-bootstrap-table/dist/react-bootstrap-table-all.min.css';
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table';
import {Button} from "react-bootstrap";
import {
    cellEditProp,
    selectRowProp,
    onAddRow,
    onCellEdit,
    onDeleteRow,
    noButtonClick,
    update
} from '../helper/TableHelper'

class Table extends Component {
    constructor(props) {
        super(props);
        this.state = {
            data: props.data,
            changed: props.changed,
            filename: props.filename,
            csvFileName: props.filename.split('.')[0] + ".csv",
            updateList: {}
        };
    }

    render() {
        let tables = [];
        let changed = false;
        for (let i in this.state.data) {
            let result = this.state.data[i];
            if (result.content.length === 0) continue;
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

            const updateSelectRowProp = {
                mode: "checkbox",
                clickToSelect: true,
                onSelect: onRowSelect.bind(this, result.source),
                onSelectAll: onSelectAll.bind(this, result.source)
            };

            let saveChangesButton;
            let table;
            let changedForm;
            if (this.state.changed[this.state.data.indexOf(result)] === true) {
                changed = true;
                changedForm =
                    <div style={{marginBottom:"5px"}}>
                        <h3>We detected that these rows changed in this sheet. Would you like to update them?</h3>
                        <Button onClick={update.bind(this, result.source, "ok")} bsStyle="primary">Update</Button>
                    </div>;
                table = <BootstrapTable ref="table" replace={true}
                                        data={result.content}
                                        selectRow={updateSelectRowProp}
                                        striped hover>
                    {tableData}
                </BootstrapTable>;
            } else if (this.state.data.length !== 0) {
                if (this.state.updateList[result.source] && this.state.updateList[result.source].length !== 0) {
                    saveChangesButton = <div style={{marginBottom:"5px"}}>
                        <h2>We noticed some changes</h2>
                        <Button onClick={update.bind(this, result.source)}>Save changes</Button>
                        <br/>
                    </div>
                }
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
                <div key={i}>
                    <h2>{result.source}</h2>
                    {saveChangesButton}
                    {changedForm}
                    {table}
                </div>);
        }

        let doNotUpdateButton;
        if(changed){
            doNotUpdateButton = <Button bsStyle="info" onClick={noButtonClick.bind(this)}>Don't update anything</Button>;
        }

        return (
            <div>
                <h1>{this.state.filename}</h1>
                {doNotUpdateButton}
                {tables}
            </div>
        );
    }
}

function onRowSelect(sourcename, row, isSelected) {
    let updateList = this.state.updateList;
    if (isSelected) {
        if (updateList[sourcename]) {
            updateList[sourcename] = [...updateList[sourcename], row];
        } else {
            updateList[sourcename] = [row];
        }
    } else {
        let index = updateList[sourcename].indexOf(row);
        if (index !== -1) {
            updateList[sourcename].splice(index, 1);
        }
    }
    this.setState({
        updateList: updateList
    });
    console.log(updateList);
    return isSelected;
}

function onSelectAll(sourcename, isSelected, currentDisplayAndSelectedData) {
    let updateList = this.state.updateList;
    if (isSelected) {
        updateList[sourcename] = currentDisplayAndSelectedData;
    } else {
        currentDisplayAndSelectedData.forEach(elem => {
            let index = updateList[sourcename].indexOf(elem);
            if (index !== -1) {
                updateList[sourcename].splice(index, 1);
            }
        });
    }
    console.log(updateList);
    this.setState({
        updateList: updateList
    });
}

export default Table;