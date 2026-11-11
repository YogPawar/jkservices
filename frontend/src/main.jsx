import React from 'react'; import ReactDOM from 'react-dom/client';
import {BrowserRouter} from 'react-router-dom'; import {CssBaseline,ThemeProvider,createTheme} from '@mui/material'; import App from './App.jsx';
const theme=createTheme({palette:{primary:{main:'#286044'},secondary:{main:'#d99a28'},background:{default:'#f5f7f3'}},shape:{borderRadius:12}});
ReactDOM.createRoot(document.getElementById('root')).render(<React.StrictMode><ThemeProvider theme={theme}><CssBaseline/><BrowserRouter><App/></BrowserRouter></ThemeProvider></React.StrictMode>);
