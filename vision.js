var num_cols;
var num_rows;
var cell_width;
var cell_height;
var pixel_width;
var pixel_height;
var canvas;
var context;

//2d array of integers, tracks which cells are empty (0) or filled (1)
//filled cells represent holes
var cells;

function init(){    
    //set number of rows, number of columns, cell width, cell height
    update(10,10,50,50);

    //create canvas and add event listener for mouse click
    canvas = document.createElement("canvas");
	canvas.id = "canvas";
	document.body.appendChild(canvas);      
    canvas.width = pixel_width;
    canvas.height = pixel_height;
    canvas.addEventListener("click", onClick, false);  
    
    //get context
    context = canvas.getContext("2d");
        
    initCells();                
}

function update(rows, cols, c_width, c_height){
    num_rows = rows;
    num_cols = cols;
    cell_width = c_width;
    cell_height = c_height;
    pixel_width = 1 + (num_cols * cell_width);
    pixel_height = 1 + (num_rows * cell_height);
}

function initCells(){
    cells = new Array(num_cols);
    for (var i = 0; i < num_cols; i++){
        cells[i] = new Array(num_rows);
        for(var j = 0; j < num_rows; j++){
            cells[i][j] = 0;
        }    
    }
    redraw();
}

function onClick(e){
    //covert pixel coords to cell
    var x;
    var y;
    if (e.pageX != undefined && e.pageY != undefined) {
	x = e.pageX;
	y = e.pageY;
    }
    else {
	x = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
	y = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
    }
    x -= canvas.offsetLeft;
    y -= canvas.offsetTop;
    x = Math.min(x, num_cols * cell_width);
    y = Math.min(y, num_rows * cell_height);

    //row and column of cell clicked
    row = Math.floor(x/cell_width);
    col = Math.floor(y/cell_height);

    //switch cell from empty to filled or vice versa
    if(cells[row][col] == 0){
        cells[row][col] = 1;
    }
    else cells[row][col] = 0;

    redraw();
}

function redraw(){
    drawGrid();
    drawCells();
}

function drawGrid(){
    context.clearRect(0, 0, pixel_width, pixel_height);
    context.beginPath();
        
    //vert lines
    for (var x = 0; x <= pixel_width; x += cell_width) {
	context.moveTo(0.5 + x, 0);
	context.lineTo(0.5 + x, pixel_height);
    }
    
    //horz lines
    for (var y = 0; y <= pixel_height; y += cell_height) {
	context.moveTo(0, 0.5 + y);
	context.lineTo(pixel_width, 0.5 +  y);
    }
    
    //draw lines
    context.strokeStyle = "#ccc";
    context.stroke();   
}

function drawCells(){
    for(var i = 0; i < num_cols; i++){
        for(var j = 0; j < num_rows; j++){
            if(cells[i][j] == 1){
                context.fillRect(i * cell_width, j * cell_height, cell_width, cell_height);
            }                  
        }
    }
}

function countHoles () {
    var external_corners = 0;
    var internal_corners = 0;    
    
    for (var i = 0; i < num_cols-1; i++){
        for(var j = 0; j < num_rows-1; j++){
            var temp = cells[i][j] + cells[i][j+1] + cells[i+1][j] + cells[i+1][j+1];
        
            if (temp == 3){
                internal_corners++;
            }
            else if (temp == 1){
                external_corners++;
            }
            
        }
    }

    var num_holes = (external_corners - internal_corners)/4;
    alert("Number of holes: " + num_holes);
}
