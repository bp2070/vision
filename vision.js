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
//var cells;
var grid;

function Grid(r, c){
    this.rows = r;
    this.cols = c;
    this.cells = new Array(this.cols);
    for (var i = 0; i < this.cols; i++){
        this.cells[i] = new Array(this.rows);
        for(var j = 0; j < this.rows; j++){
            this.cells[i][j] = 0;
        }    
    }

    this.each = function(x) {
        for (var i = 0; i < this.cols; i++){
            for (var j = 0; j < this.rows; j++){
                x(i, j);
            }
        }
    }
}

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
        
    grid = new Grid(num_rows, num_cols);                
    redraw();
}

function update(rows, cols, c_width, c_height){
    num_rows = rows;
    num_cols = cols;
    cell_width = c_width;
    cell_height = c_height;
    pixel_width = 1 + (num_cols * cell_width);
    pixel_height = 1 + (num_rows * cell_height);
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
    if(grid.cells[row][col] == 0){
        grid.cells[row][col] = 1;
    }
    else grid.cells[row][col] = 0;

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
            if(grid.cells[i][j] == 1){
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
            var temp = grid.cells[i][j] + grid.cells[i][j+1] + grid.cells[i+1][j] + grid.cells[i+1][j+1];
        
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

function labelHoles(labelElement){
    var label_grid = new Grid(num_rows, num_cols);
    var label = 1;
    var parent_arr = new Array();
    parent_arr.push(0);

    //1st pass - for each cell in the grid 
    label_grid.each(function(j, i) {
        //if cell is filled
        if(grid.cells[i][j] == 1){
           
            var pn = prior_neighbors(i, j, label_grid.cells);

            //if pn is empty min = ++label
            var empty = true;
            var min = label;
            for(var k = 0; k < 4; k++){
               var x= pn[k];
               if (x != 0){
                   empty = false;
                   if (x < min){
                       min = x;
                   }
                   //if (x != label){
                    //   parent_arr[x] = label;
                   //}
               }
            }
            if(empty){
                parent_arr.push(0);
                label++;
            }

            //assign label 
            label_grid.cells[i][j] = min;

            //union labels
            for(var k = 0; k < 4; k++){
                var x = pn[k];
                if (x != min){
                    union(min, x, parent_arr);
                }
            }
        }
    });

    //2nd pass - for each cell in the grid
    label_grid.each(function(i, j) {
        //if cell is filled
        if(grid.cells[i][j] == 1){
            //assign parent label
            label_grid.cells[i][j] = find(label_grid.cells[i][j], parent_arr);
        }
    });

    var label_str = "";
    /*
    for(var i = 0; i < label_grid.rows; i++){
        for(var j = 0; j < label_grid.cols; j++){
            label_str += label_grid.cells[i][j] + " "
        }
        label_str += "\n";
    }
    */
    labelElement.innerHTML = label_str;
}

function find(i, parent_arr){
    while (parent_arr[i] != 0){
        i = parent_arr[i];
    }
    return i;
}

function union(x, y, parent_arr){
    j = find(x, parent_arr);
    k = find(y, parent_arr);
    if (j != k){
        parent_arr[k] = j;
    }
}
function prior_neighbors(i, j, cells){
            //prior neighbors
            var prior_neighbors = new Array(4);
           
            //ignore ioob exceptions 
            try{
                prior_neighbors[0] = cells[i-1][j-1];
            }catch(e){
                prior_neighbors[0] = 0;
            }
            try{
                prior_neighbors[1] = cells[i][j-1];
            }catch(e){
                prior_neighbors[1] = 0;
            }
            try{
                prior_neighbors[2] = cells[i+1][j-1];
            }catch(e){
                prior_neighbors[2] = 0;
            }
            try{
                prior_neighbors[3] = cells[i-1][j];
            }catch(e){
                prior_neighbors[3] = 0;
            }
            
            return prior_neighbors;
}
