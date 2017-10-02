function noButtonClick() {
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
            data: [],
            changed: json.changed
        });
        this.setState({
            data: json.results
        })
    }).catch(function (ex) {
        console.log('parsing failed', ex)
    });
}

function update(sourcename) {
    let update = this.state.updateList[sourcename];
    if (!update) return;
    let hashes = update.map(content => content.hash);
    fetch('http://localhost:8080/api/fileload/' + this.state.filename + '/source/' + sourcename + '/content/' + hashes + '/update', {
        method: 'PATCH',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({values: update}),
    }).then(response => {
            if (response.status === 200) {
                return response.json();
            }
            throw new Error(response.statusText);
        }
    ).then(json => {
        let updateList = this.state.updateList;
        let hashes = updateList[sourcename].map(elem => elem.hash);
        updateList[sourcename] = [];

        let result = this.state.data.filter(dat => dat.source === sourcename)[0];
        result.content = result.content.filter(cont => !hashes.includes(cont.hash));

        let data = this.state.data;
        let index = data.findIndex(dat => dat.source === sourcename);
        if (index !== -1)
            data[index] = result;

        this.setState({
            updateList: updateList,
            data: data
        });
        console.log(this.state.data);
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

function onAddRow(sourcename, row) {
    fetch('http://localhost:8080/api/fileload/' + this.state.filename + '/source/' + sourcename + '/content/insert', {
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
        let result = data.find(d => {
            return d.source === sourcename;
        });

        result.content.push(json);
        this.setState({
            data: data
        })
    }).catch(function (ex) {
        console.log('parsing failed', ex)
    });
}

function onDeleteRow(sourcename, ids, rows) {
    fetch('http://localhost:8080/api/fileload/' + this.state.filename + '/source/' + sourcename + '/content/' + ids + '/delete', {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
        }
    }).then(response => {
            if (response.status === 200) {
                let data = this.state.data;
                console.log(data);
                let result = data.find(d => {
                    return d.source === sourcename;
                });
                let index = data.indexOf(result);
                let content = result.content;
                content = content.filter(value => {
                    return !ids.includes(value.hash);
                });
                console.log(content);
                result.content = content;
                data[index] = result;
                console.log(data);
                this.setState({
                    data: data,
                    changed: this.state.changed
                })
            }
        }
    ).catch(function (ex) {
        console.log('parsing failed', ex)
    });
}

function onCellEdit(sourcename, row, column, value) {
    let data = this.state.data;
    let updateList = this.state.updateList;
    let result = this.state.data.filter(dat => dat.source === sourcename)[0];
    let rowIndex = result.content.indexOf(row);
    let resultIndex = data.indexOf(result);
    row[column] = value;
    result.content[rowIndex] = row;
    data[resultIndex] = result;

    if (updateList[sourcename]) {
        updateList[sourcename] = [...updateList[sourcename], row];
    } else {
        updateList[sourcename] = [row];
    }
    this.setState({
        data: data,
        updateList: updateList
    });
}

const cellEditProp = {
    //beforeSaveCell: beforeSaveCell,
    mode: 'click'
};

const selectRowProp = {
    mode: 'checkbox'
};

export {
    cellEditProp,
    selectRowProp,
    updateButtonClick,
    onAddRow,
    onCellEdit,
    onDeleteRow,
    noButtonClick,
    update
}