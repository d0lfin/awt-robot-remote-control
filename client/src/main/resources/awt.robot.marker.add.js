/**
 * bookmarklet for add marker
 */
(function(){
    var node = document.createElement("div"),
        child1 = document.createElement("div"),
        child2 = document.createElement("div");
    
    node.style.width = "1px";
    node.style.height = "3px";
    node.style.backgroundColor = "#CD0074";
    node.style.position = "fixed";
    node.style.left = 0;
    node.style.top = 0;
    
    child1.style.width = "1px";
    child1.style.height = "1px";
    child1.style.backgroundColor = "#00CC00";

    child2.style.width = "1px";
    child2.style.height = "1px";
    child2.style.backgroundColor = "#FF7400";

    node.appendChild(clearStyle(child1));
    node.appendChild(clearStyle(child2));

    if(!window.awtrobotmarker){
        document.getElementsByTagName("body")[0].appendChild(clearStyle(node));
        window.awtrobotmarker = node;
    }

    function clearStyle(node){
        node.style.padding = 0;
        node.style.margin = 0;
        node.style.border = "none";
        return node;
    }
})();