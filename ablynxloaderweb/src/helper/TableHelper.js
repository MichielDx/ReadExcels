function noButtonClick(){
    fetch('http://localhost:8080/api/fileload/' + this.state.filename + '/content', {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    }).then(response => {
            if (response.status === 200) {
                return response.json();
            }
            throw new Error(response.statusText);
        }
    ).then(json => {
        this.setState({
            data:[],
            changed: json.changed
        });
        this.setState({
            data: json.values
        })
    }).catch(function (ex) {
        console.log('parsing failed', ex)
    });
}

function updateButtonClick() {
    fetch('http://localhost:8080/api/fileload/' + this.state.filename + '/content/update', {
        method: 'PATCH',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({values: this.state.data}),
    }).then(response => {
        if (response.status === 200) {
            return response.json();
        }
        throw new Error(response.statusText);
    }).then(json => {
        return this.setState({
            data: json.values,
            changed: json.changed,
            filename: this.state.filename,
            showTable: true
        });
    }).catch(function (ex) {
        console.log('parsing failed', ex)
    });
}

function onAddRow(row) {
    fetch('http://localhost:8080/api/fileload/' + this.state.filename + '/content/insert', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({value: row}),
    }).then(response => {
            if (response.status === 200) {
                return response.json();
            }
            throw new Error(response.statusText);
        }
    ).then(json => {
        let data = this.state.data;
        data.push(json);
        this.setState({
            data: data
        })
    }).catch(function (ex) {
        console.log('parsing failed', ex)
    });
}

function onDeleteRow(ids, rows) {
    fetch('http://localhost:8080/api/fileload/' + this.state.filename + '/content/' + ids + '/delete', {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
        }
    }).then(response => {
            if (response.status === 200) {
                let data = this.state.data;
                data = data.filter(value => !ids.includes(value.hash));
                this.setState({
                    data: data,
                    changed: undefined
                })
            }
        }
    ).catch(function (ex) {
        console.log('parsing failed', ex)
    });
}

function onCellEdit(row, column, value) {
    let newRow = row;
    newRow[column] = value;
    fetch('http://localhost:8080/api/fileload/' + this.state.filename + '/content/{' + row.hash + '}/update', {
        method: 'PATCH',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({value: newRow}),
    }).then(response => {
            if (response.status === 200) {
                return response.json();
            }
            throw new Error(response.statusText);
        }
    ).then(json => {
        let index = this.state.data.indexOf(row);
        let data = this.state.data;
        data[index] = json;
        this.setState({
            data: data
        });
    }).catch(function (ex) {
        console.log('parsing failed', ex)
    });


}

const cellEditProp = {
    //beforeSaveCell: beforeSaveCell,
    mode: 'click'
};

const selectRowProp = {
    mode: 'checkbox'
};

export {cellEditProp, selectRowProp, updateButtonClick, onAddRow, onCellEdit, onDeleteRow, noButtonClick}