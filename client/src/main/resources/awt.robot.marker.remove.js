/**
 * bookmarklet for remove marker
 */
(function(){
    if(window.awtrobotmarker){
        document.getElementsByTagName("body")[0].removeChild(window.awtrobotmarker);
        window.awtrobotmarker = null;
    }
})();