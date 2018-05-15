import { StyleSheet} from 'react-native';

export default styles=StyleSheet.create({
   
  container: {
      flex: 1,
      padding: 20,
      backgroundColor: '#34515e',
  
    },

    containerAlign: {
      flex: 1,
      padding: 20,
      backgroundColor: '#34515e',
      //alignItems moves items to upper center
      alignItems: 'center',
      //justifyContent moves items to center of page
      justifyContent: 'center'
  
    },

    containerBottomAlign: {
      flex: 1,
      padding: 20,
      backgroundColor: '#34515e',
      //alignItems moves items to upper center
      alignItems: 'baseline',
      //justifyContent moves items to center of page
      justifyContent: 'center'
  
    },

    //Text input boxes
    input: {
      height: 40,
      width: 200,
      color: '#FFF',
      backgroundColor: '#607d8b',
      marginBottom: 10,
      paddingHorizontal: 10
    },
    //normal button
    buttonContainer: {
      backgroundColor: '#62757f',
      paddingVertical: 15,
      width: 200,
      
    },
    buttonText: {
      textAlign: 'center',
      color: '#FFF',
      fontWeight: '700'
  
    },
    //add button
    addButton: {
       alignItems:'center',
       justifyContent:'center',
       width:60,
       height:60,
       backgroundColor:'#62757f',
       borderRadius:60,
    
    },
    error: {
      color: 'red'
    },

    textLarge: {
      textAlign: 'center',
      color: '#FFF',
      fontSize: 20
    },

    text: {
      color: '#FFF',
      fontSize: 15
    }
   
  });