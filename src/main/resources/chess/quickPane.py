
colour = ["d3d3d3", "1c1f1d"]
for i in range(0, 8):
    for j in range(0, 8):
        string = f'{" "*9}<Pane fx:id="pane{i}{j}" prefHeight="200.0" prefWidth="200.0" style="-fx-background-color: #{colour[(j+i)%2]};" GridPane.columnIndex="{j}" GridPane.rowIndex="{i}" >'
        string += f'\n{" "*12}<children>\n{" "*15}<ImageView fx:id="image{i}{j}" fitHeight="100.0" fitWidth="100.0" pickOnBounds="true" preserveRatio="true" />'
        string += f'\n{" "*12}</children></Pane>'
        print(string)


""" 
<children>
   <ImageView id="image00" fitHeight="100.0" fitWidth="100.0" pickOnBounds="true" preserveRatio="true" />
</children></Pane>
               """