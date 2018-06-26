import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, FlatList} from 'react-native';
import styles from '../Login/Design';
import {setState} from '../Login/state';
import {URL} from '../Login/const';
import {StackNavigator,} from 'react-navigation';
import {getAuth} from '../Login/auth';

export var projectname = '';
export var projectstatus = '';

export default class ProjectList extends Component {
    static navigationOptions= {
		title: 'Project Overview',
		headerStyle: {
			backgroundColor:'#5daedb'
		},
		headerTitleStyle: {
			color:'#FFF'
		}
    } 

    constructor(props) {
		super(props);
		this.state = {
			userProjects: [],
		};
	}

    componentDidMount() {
        this.fetchUserProjects();
}
    async onAddProject() {

         const { navigate } = this.props.navigation;
          navigate("Third", { name: "JoinProject" })
    
       } 
     
       fetchUserProjects() {
        fetch(URL + '/projects', {method:'GET', headers: getAuth()})
				.then((response) => response.json())
					.then((responseJson) => {
						this.setState({
							userProjects: responseJson
						}, function() {});
					}).catch((error) => {
						console.error(error);
					});
       }       

 _renderProjects({item}) {
    if (item.finished === 'false') {
        projectstatus = 'Project is finished';
    } else {
        projectstatus = 'Project is open';
    }
     return (
        <TouchableOpacity
                     style={styles.buttonLargeContainer}>
                     <Text style={styles.buttonText}>
                    Project Name: {item.name} 
                    </Text>
                    <Text style={styles.buttonText}>
                    Project Status: {projectstatus}
                    </Text>
                      </TouchableOpacity>
     );
 }      
      
render() {
    return (
      <View style={styles.container}>
          <TouchableOpacity 
         onPress={this.onAddProject.bind(this)} 
          style={styles.buttonLargeContainer}>
          
              <Text style={styles.buttonText}>Add Project</Text>
        
          </TouchableOpacity>
          <FlatList
					style={styles.textLarge}
					data={this.state.userProjects}
                    renderItem={this._renderProjects.bind(this)}
					 keyExtractor={(item, index) => index}
				/>   

      </View>
    );
  }
}