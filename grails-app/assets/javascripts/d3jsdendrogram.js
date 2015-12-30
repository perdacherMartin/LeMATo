// Code obtained and modified from http://bl.ocks.org/robschmuecker/7880033#dndTree.js
// d3js.org

//= require /webjars/d3js/3.5.6/d3.min.js

/**
 * insert the d3js-dendrogram into the following div-id. The data has already the json format.
 * @param [div-id] [string]
 * @param [json] [json data]
 */
function dendrogram(divId, json) {
    // Calculate total nodes, max label length
    var totalNodes = 0;
    var maxLabelLength = 0;
    // variables for drag/drop
    var selectedNode = null;
    var draggingNode = null;
    // panning variables
    var panSpeed = 200;
    var panBoundary = 20; // Within 20px from edges will pan when dragging.
    // Misc. variables
    var i = 0;
    var duration = 750;
    var root;

    // size of the diagram
    var viewerWidth = $(document).width();
    var viewerHeight = $(document).height() / 5;

    var tree = d3.layout.tree()
        .size([viewerHeight, viewerWidth]);

    // define a d3 diagonal projection for use by the node paths later on.
    var diagonal = d3.svg.diagonal()
        .projection(function(d) {
            return [d.y, d.x];
        });

    // A recursive helper function for performing some setup by walking through all nodes

    function visit(parent, visitFn, childrenFn) {
        if (!parent) return;

        visitFn(parent);

        var children = childrenFn(parent);
        if (children) {
            var count = children.length;
            for (var i = 0; i < count; i++) {
                visit(children[i], visitFn, childrenFn);
            }
        }
    }

    // Call visit function to establish maxLabelLength
    visit(json, function(d) {
        totalNodes++;
        maxLabelLength = Math.max(d.name.length, maxLabelLength);

    }, function(d) {
        return d.children && d.children.length > 0 ? d.children : null;
    });


    // sort the tree according to the node names

    function sortTree() {
        tree.sort(function(a, b) {
            return b.name.toLowerCase() < a.name.toLowerCase() ? 1 : -1;
        });
    }
    // Sort the tree initially incase the JSON isn't in a sorted order.
    sortTree();

    // TODO: Pan function, can be better implemented.

    function pan(domNode, direction) {
        var speed = panSpeed;
        if (panTimer) {
            clearTimeout(panTimer);
            translateCoords = d3.transform(svgGroup.attr("transform"));
            if (direction == 'left' || direction == 'right') {
                translateX = direction == 'left' ? translateCoords.translate[0] + speed : translateCoords.translate[0] - speed;
                translateY = translateCoords.translate[1];
            } else if (direction == 'up' || direction == 'down') {
                translateX = translateCoords.translate[0];
                translateY = direction == 'up' ? translateCoords.translate[1] + speed : translateCoords.translate[1] - speed;
            }
            scaleX = translateCoords.scale[0];
            scaleY = translateCoords.scale[1];
            scale = zoomListener.scale();
            svgGroup.transition().attr("transform", "translate(" + translateX + "," + translateY + ")scale(" + scale + ")");
            d3.select(domNode).select('g.node').attr("transform", "translate(" + translateX + "," + translateY + ")");
            zoomListener.scale(zoomListener.scale());
            zoomListener.translate([translateX, translateY]);
            panTimer = setTimeout(function() {
                pan(domNode, speed, direction);
            }, 50);
        }
    }

    // Define the zoom function for the zoomable tree

    function zoom() {
        svgGroup.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
    }


    // define the zoomListener which calls the zoom function on the "zoom" event constrained within the scaleExtents
    var zoomListener = d3.behavior.zoom().scaleExtent([0.1, 3]).on("zoom", zoom);


    // define the baseSvg, attaching a class for styling and the zoomListener
    var baseSvg = d3.select(divId).append("svg")
        .attr("width", viewerWidth)
        .attr("height", viewerHeight)
        .attr("class", "overlay")
        .call(zoomListener);


    // Helper functions for collapsing and expanding nodes.

    function collapse(d) {
        if (d.children) {
            d._children = d.children;
            d._children.forEach(collapse);
            d.children = null;
        }
    }

    function expand(d) {
        if (d._children) {
            d.children = d._children;
            d.children.forEach(expand);
            d._children = null;
        }
    }


    // Function to center node when clicked/dropped so node doesn't get lost when collapsing/moving with large amount of children.

    function centerNode(source) {
        scale = zoomListener.scale();
        x = -source.y0;
        y = -source.x0;
        x = x * scale + viewerWidth / 2;
        y = y * scale + viewerHeight / 2;
        d3.select('g').transition()
            .duration(duration)
            .attr("transform", "translate(" + x + "," + y + ")scale(" + scale + ")");
        zoomListener.scale(scale);
        zoomListener.translate([x, y]);
    }

    // Toggle children function

    function toggleChildren(d) {
        if (d.children) {
            d._children = d.children;
            d.children = null;
        } else if (d._children) {
            d.children = d._children;
            d._children = null;
        }
        return d;
    }

    // Toggle children on click.

    function click(d) {
        if (d3.event.defaultPrevented) return; // click suppressed

        d = toggleChildren(d);
        update(d);
        centerNode(d);
    }

    function contextmenu(d,i) {

        var getLeaveTd = function(d){
            var text = ""
            if ( d.children || d._children ){ // hasChildren
                if ( !d._children ){                    
                    d.children.forEach(function(child){
                            text = text.concat(getLeaveTd(child));
                        }
                    )
                }else{                    
                    d._children.forEach(function(child){                            
                            text = text.concat(getLeaveTd(child));
                        }
                    )
                }
            }else{
                // leave node     
                text = text.concat('<tr>');
                text = text.concat( '<td>', d.name, '</td>\n', '<td>', d.docCount, '</td>\n', '<td>', d.score, '</td>\n' );
                text = text.concat('<td><a class="row-delete" href="#"><span class="glyphicon glyphicon-remove"></span></a></td>\n');
                text = text.concat('</tr>');
            }

            return text;
        }
        
        d3_target = d3.select(d3.event.target);
        // console.log (" target ", d3_target);

        d3.event.preventDefault();
        d = d3_target.datum();
        
        var myWindow = window.open(null, "MsgWindow", "scrollbars=1, width=300, height=200");
        myWindow.document.write(
            "<html>",
            "<head>",
                "<script type=\"text/javascript\" src=\"https://cdn.jsdelivr.net/jquery/3.0.0-alpha1/jquery.min.js\"></script>",
                "<script type=\"text/javascript\" src=\"https://cdn.jsdelivr.net/jquery.footable/2.0.3/footable.min.js\"></script>",
                "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/jquery.footable/2.0.3/css/footable.core.min.css\">",
                "<script type=\"text/javascript\" src=\"https://cdn.jsdelivr.net/bootstrap/3.3.5/js/bootstrap.min.js\"></script>",
                "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/bootstrap/3.3.5/css/bootstrap.min.css\">",
                "<script type=\"text/javascript\" src=\"https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.9.1/bootstrap-table.min.js\"></script>",
            "</head>",
            "<body>",
            "<table id=\"termtable\" class=\"table table-hover\">",
                "<thead>",
                    "<tr>",
                        "<th data-sortable=\"true\" >term</th>",
                        "<th data-sortable=\"true\" >textunit frequency</th>",
                        "<th data-sortable=\"true\" >score</th>",
                        "<th>remove</th>",
                    "</tr>",
                "</thead>",
                "<tbody>",
                    getLeaveTd(d),
                "</tbody>",
            "</table>",
            "<script type=\"text/javascript\">",
                "$('table').footable().on('click', '.row-delete', function(e) {",
                    "console.log('clicked');",
                    "e.preventDefault();",
                    "var footable = $('table').data('footable');",
                    "var row = $(this).parents('tr:first');",
                    "footable.removeRow(row);",
                "});",
                "$('#termtable').bootstrapTable({});",
                "$('#termtable').bootstrapTable('hideLoading');",
            "</script>",
            "</body>",
            "</html>"
        );

        // var popup = $("<div></div>").append("h2").text("Leave Text");
        // popup.append("p").text("hello world!");
        // // var popup = $("<div id=\"dialog\" title=\"Basic dialog\"></div>").append("<p>this is a test!</p>");

        // popup.dialog("open");
        // <table class=\"footable table\"> \
        //     <thead> \
        //     <tr> \
        //     </tr> \
        //     </thead> \
        //     </table>");

        //     popup.append("<th>term</th>");
        //     popup.append("<th>textunit frequency</th>");
        //     popup.append("<th>score</th>");
        //     popup.append("<th>remove</th>");
        // popup.append("</tr>");
        // popup.append("</thead>");
        // popup.append("<tbody>");
        //     popup.append("<tr>");
        //     popup.append(getLeaveTd(d));
        //     popup.append("</tr>");
        // popup.append("</tbody>");
        // popup.append("</div>");
        
        // console.log('<<< popup: >>>', popup.html());
        // if (d3_target.classed("nodeCircle")) {
        //     console.log ("D3_TARGET", d3_target);
        //     d3.event.preventDefault();
        //     contextMenuShowing = true;
        //     d = d3_target.datum();
        //     // Build the popup
            
        //     // canvas = d3.select("#dendro-container-1");
        //     canvas = $(d3_target[0]).parents(".container");
        //     canvas = d3.select(canvas[0].id);
        //     infoId = "info-" + canvas[0].id;

        //     console.log ("+++++xx+++", canvas);
            // mousePosition = d3.mouse(canvas.node());
            
            // popup = canvas.append("div")
            //     .attr("class", "popup")
            //     .attr("id", infoId)
            //     .style({
            //         left: "100px",
            //         top: "100px",
            //         width: "300px",
            //         position: "relative",
            //         "z-index": 1000,
            //         "background-color": "gray"
            //     });
            //     // .style("left", mousePosition[0] + "px")
            //     // .style("top", mousePosition[1] + "px");
            
            // popup.append("h2").text("Leave Text");
            // popup.append("p").text(getLeaveText(d));

            // JQuery Dialog:
            // jquery table: http://fooplugins.com/footable/demos/add-delete-row.htm
            
            // // Window Popup:
            // var myWindow = window.open(null, "MsgWindow", "width=200, height=100");
            // myWindow.document.write(getLeaveText(d));
            // console.log ("popup...", popup);
        // }

        // alert( getLeaveText(d) )
    }

    function update(source) {
        // Compute the new height, function counts total children of root node and sets tree height accordingly.
        // This prevents the layout looking squashed when new nodes are made visible or looking sparse when nodes are removed
        // This makes the layout more consistent.
        var levelWidth = [1];
        var childCount = function(level, n) {

            if (n.children && n.children.length > 0) {
                if (levelWidth.length <= level + 1) levelWidth.push(0);

                levelWidth[level + 1] += n.children.length;
                n.children.forEach(function(d) {
                    childCount(level + 1, d);
                });
            }
        };
        childCount(0, root);
        var newHeight = d3.max(levelWidth) * 25; // 25 pixels per line  
        tree = tree.size([newHeight, viewerWidth]);

        // Compute the new tree layout.
        var nodes = tree.nodes(root).reverse(),
            links = tree.links(nodes);

        // Set widths between levels based on maxLabelLength.
        nodes.forEach(function(d) {
            d.y = (d.depth * (maxLabelLength * 10)); //maxLabelLength * 10px
            // alternatively to keep a fixed scale one can set a fixed depth per level
            // Normalize for fixed-depth by commenting out below line
            // d.y = (d.depth * 500); //500px per level.
        });

        // Update the nodes…
        node = svgGroup.selectAll("g.node")
            .data(nodes, function(d) {
                return d.id || (d.id = ++i);
            });

        // Enter any new nodes at the parent's previous position.
        var nodeEnter = node.enter().append("g")
            .attr("class", "node")
            .on('click', click)
            .on('contextmenu', contextmenu);

        nodeEnter.append("circle")
            .attr('class', 'nodeCircle')
            .attr("r", 0)
            .style("fill", function(d) {
                return d._children ? "lightsteelblue" : "#fff";
            });

        nodeEnter.append("text")
            .attr("x", function(d) {
                return d.children || d._children ? -10 : 10;
            })
            .attr("dy", ".35em")
            .attr('class', 'nodeText')
            .attr("text-anchor", function(d) {
                return d.children || d._children ? "end" : "start";
            })
            .text(function(d) {
                return d.name;
            })
            .style("fill-opacity", 0);

        // phantom node to give us mouseover in a radius around it
        nodeEnter.append("circle")
            .attr('class', 'ghostCircle')
            .attr("r", 30)
            .attr("opacity", 0.2) // change this to zero to hide the target area
        .style("fill", "red")
            .attr('pointer-events', 'mouseover')
            .on("mouseover", function(node) {
                overCircle(node);
            })
            .on("mouseout", function(node) {
                outCircle(node);
            });

        // Update the text to reflect whether node has children or not.
        node.select('text')
            .attr("x", function(d) {
                return d.children || d._children ? -10 : 10;
            })
            .attr("text-anchor", function(d) {
                return d.children || d._children ? "end" : "start";
            })
            .text(function(d) {
                return d.name;
            });

        // Change the circle fill depending on whether it has children and is collapsed
        node.select("circle.nodeCircle")
            .attr("r", 4.5)
            .style("fill", function(d) {
                return d._children ? "lightsteelblue" : "#fff";
            });

        // Transition nodes to their new position.
        var nodeUpdate = node.transition()
            .duration(duration)
            .attr("transform", function(d) {
                return "translate(" + d.y + "," + d.x + ")";
            });

        // Fade the text in
        nodeUpdate.select("text")
            .style("fill-opacity", 1);

        // Transition exiting nodes to the parent's new position.
        var nodeExit = node.exit().transition()
            .duration(duration)
            .attr("transform", function(d) {
                return "translate(" + source.y + "," + source.x + ")";
            })
            .remove();

        nodeExit.select("circle")
            .attr("r", 0);

        nodeExit.select("text")
            .style("fill-opacity", 0);

        // Update the links…
        var link = svgGroup.selectAll("path.link")
            .data(links, function(d) {
                return d.target.id;
            });

        // Enter any new links at the parent's previous position.
        link.enter().insert("path", "g")
            .attr("class", "link")
            .attr("d", function(d) {
                var o = {
                    x: source.x0,
                    y: source.y0
                };
                return diagonal({
                    source: o,
                    target: o
                });
            });

        // Transition links to their new position.
        link.transition()
            .duration(duration)
            .attr("d", diagonal);

        // Transition exiting nodes to the parent's new position.
        link.exit().transition()
            .duration(duration)
            .attr("d", function(d) {
                var o = {
                    x: source.x,
                    y: source.y
                };
                return diagonal({
                    source: o,
                    target: o
                });
            })
            .remove();

        // Stash the old positions for transition.
        nodes.forEach(function(d) {
            d.x0 = d.x;
            d.y0 = d.y;
        });
    }

    // Append a group which holds all nodes and which the zoom Listener can act upon.
    var svgGroup = baseSvg.append("g");

    // Define the root
    root = json;
    root.x0 = viewerHeight / 2;
    root.y0 = 0;

    // Layout the tree initially and center on the root node.
    update(root);
    centerNode(root);	
}