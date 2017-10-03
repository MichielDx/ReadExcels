import React, {Component} from 'react';
import './App.css';
import '../../node_modules/react-bootstrap-table/dist/react-bootstrap-table-all.min.css';
import Dropzone from 'react-dropzone'
import Table from '../component/Table'

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            showTable: false,
            changed: false,
            filename: "",
            data: []
        };
    }

    onDrop(files) {
        let data = files[0];
        let formData = new FormData();
        formData.append("name", data.name);
        formData.append("file", data);
        fetch('http://localhost:8080/api/fileload/upload', {
            method: 'POST',
            body: formData,
        }).then(function (response) {
            return response.json()
        }).then(json => {
            this.setState({
                data:json.results,
                changed:json.changed,
                filename:data.name,
                showTable: true
            });
        }).catch(function (ex) {
            console.log('parsing failed', ex)
        });

    }

    render() {
        let component;
        if (this.state.showTable === false) {
            component =
                <section>
                    <div className="dropzone">
                        <Dropzone onDrop={this.onDrop.bind(this)}>
                            <p className={"drop"}>Try dropping some files here, or click to select files to upload.</p>
                        </Dropzone>
                    </div>
                </section>
        } else {
            component = <Table data={this.state.data} changed={this.state.changed} filename={this.state.filename}/>
        }
        return (
            <div>
                <div width="100vh"><img alt="logo" className="logo" src={require("../images/ablynx_logo_large.gif")}/></div>
                {component}
            </div>
        )
    }
}

export default App;