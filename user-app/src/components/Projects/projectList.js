import React, {Component} from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, FlatList, Button} from 'react-native';
import styles from '../Login/Design';
import {setState} from '../Login/state';
import {URL} from '../Login/const';
import {getAuth, username} from '../Login/auth';
import {getUpdateBoolean, setUpdateBoolean} from '../Login/state';
import {setKey, isValid} from '../Projects/keyValid';
import Icon from 'react-native-vector-icons/FontAwesome';

export var projectname = '';
export var projectstatus = '';

export default class ProjectList extends Component {

//setting page title 
	static navigationOptions = ({ navigation }) => {
		const { params = {} } = navigation.state;
		return {
  title: 'Projects',
  headerStyle: {
    backgroundColor:'#5daedb',
    paddingRight: 15,
  },
  headerTitleStyle: {
    color:'#FFF'
  },
  headerRight: <Icon name="user" size={30} color="#FFF"  onPress={ () => params.update() } />
} }
    constructor(props) {
		super(props);
		this.state = {
      userProjects: [],
      entryKey: "",
			info: "",
			infoType: {}
		};
	}


    componentDidMount() {
    	this.props.navigation.setParams({ update: this.updateUser })
        this.fetchUserProjects();
    }
    
    updateUser = () => {
    	const { navigate } = this.props.navigation;
    	navigate("Thirteenth", { name: "UserInfo" });
    }

    componentDidUpdate() {
    	if(getUpdateBoolean() === true) {
    		this.fetchUserProjects();
    		setUpdateBoolean(false);
    	}
    }

    async onAddProject() {

      setUpdateBoolean(true);
      setKey(this.state.entryKey);
      
      //enables blinking feedback message
      this.setState({info: "", infoType: styles.success});
  
     if(await isValid()){
        setState({isValid: true});

        this.setState({info: "Project added", infoType: styles.success});
        
        //enable for immediate navigation after pressing join
//        const { navigate } = this.props.navigation;
//        navigate("Twelfth", {entryKey: this.state.entryKey});
  
     } else {
        this.setState({info: "Project not found", infoType: styles.error});
     }
      
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
        projectstatus = 'Project is open';

     return (
        <TouchableOpacity
      onPress={()=> this.props.navigation.navigate("Twelfth", {entryKey:item.entryKey}) }
                     style={styles.buttonMediumContainer}>
                     <Text style={styles.buttonText}>
                    Project Name: {item.name} 
                    </Text>
                    <Text style={styles.buttonText}>
                    Project Status: {projectstatus}
                    </Text>
                      </TouchableOpacity>
     );
    }
 }      
      
render() {
	var buttonEnabled = (this.state.entryKey !== '');
    return (
      <View style={styles.containerAlign}>
      <TextInput 
         onChangeText={(text) => this.setState({entryKey: text})} 
        placeholder="Entry Key" placeholderTextColor="#FFF" underlineColorAndroid="transparent" autoCapitalize="none" style={styles.inputLong} onSubmitEditing={buttonEnabled ? this.onAddProject.bind(this) : null}/>
          <TouchableOpacity 
          disabled={!buttonEnabled}
         onPress={this.onAddProject.bind(this)} 
          style={styles.buttonLargeContainer}>
          
              <Text style={styles.buttonText}>Join Project</Text>
        
          </TouchableOpacity>
          <Text style={this.state.infoType}>
          {this.state.info}
				</Text>
				<Text/>
          <FlatList
					data={this.state.userProjects}
                    renderItem={this._renderProjects.bind(this)}
					 keyExtractor={(item, index) => index.toString()}
          />   
          
      </View>
    );
  }
}