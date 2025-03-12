from PIL import Image
import tkinter as tk
from tkinter import ttk, filedialog, messagebox
import json

class Application:
    def __init__(self):
        self.root = tk.Tk()
        self.frame = ttk.Frame(self.root)
        self.frame.grid()

        self.loadOverlays()

        
        if not self.validConfig():
            self.assignPaths()

        self.image = None

        self.value = tk.StringVar(self.root)

        ttk.Button(self.frame, text="Assign Paths", command=self.assignPaths).grid(column=0, row=0)
        ttk.Label(self.frame, textvariable=self.value).grid(column=0, row=1)
        ttk.Button(self.frame, text="Get Input Texture", command=self.getInputTexture).grid(column=0, row=2)
        ttk.Button(self.frame, text="Generate", command=self.generate).grid(column=0, row=3)
        ttk.Button(self.frame, text="Quit", command=self.root.destroy).grid(column=0,row=4)

        self.root.mainloop()
    
    def validConfig(self):
        try:
            with open("config.json") as f:
                result = json.load(f)
                self.block_model_dir = result["block_model_dir"]
                self.block_texture_dir = result["block_texture_dir"]
                self.item_model_dir = result["item_model_dir"]
                self.item_dir = result["item_dir"]
                self.blockstate_dir = result["blockstate_dir"]
            self.value = f"Block Model Dir: {self.block_model_dir}\nBlock Texture Dir: {self.block_texture_dir}\nItem Model Dir: {self.item_model_dir}\nItem Dir: {self.item_dir}\nBlock State Dir: {self.blockstate_dir}"
            return True
        except:
            return False
    
    def updateConfig(self):
        with open("config.json", "w") as f:
            json.dump({
                "block_model_dir": self.block_model_dir,
                "block_texture_dir": self.block_texture_dir,
                "item_model_dir": self.item_model_dir,
                "item_dir": self.item_dir,
                "blockstate_dir": self.blockstate_dir
            }, f)

    def assignPaths(self):

        self.block_model_dir = filedialog.askdirectory(mustexist=True, title="Directory to put block models")
        self.block_texture_dir = filedialog.askdirectory(mustexist=True, title="Directory to put block textures")
        self.item_model_dir = filedialog.askdirectory(mustexist=True, title="Directory to put item model")
        self.item_dir = filedialog.askdirectory(mustexist=True, title="Directory to put item")
        self.blockstate_dir = filedialog.askdirectory(mustexist=True, title="Directory to put blockstate")

        self.value = f"Block Model Dir: {self.block_model_dir}\nBlock Texture Dir: {self.block_texture_dir}\nItem Model Dir: {self.item_model_dir}\nItem Dir: {self.item_dir}\nBlock State Dir: {self.blockstate_dir}"

        self.updateConfig()

    def getInputTexture(self):
        self.image = Image.open(filedialog.askopenfilename())
    
    def generate(self):
        if (self.image == None):
            messagebox.showinfo("Error", "You did not select an inupt texture.")
            return
        
        block_model1 = ""
        block_model2 = ""
        block_model3 = ""
        block_model4 = ""
        block = ""
        item_model = ""
        item = ""
        with open("slimy_block_model_1.json", "r") as f:
            block_model1 = f.read()
        with open("slimy_block_model_2.json", "r") as f:
            block_model2 = f.read()
        with open("slimy_block_model_3.json", "r") as f:
            block_model3 = f.read()
        with open("slimy_block_model_4.json", "r") as f:
            block_model4 = f.read()
        with open("slimy_block.json", "r") as f:
            block = f.read()
        with open("slimy_item_model.json", "r") as f:
            item_model = f.read()
        with open("slimy_item.json", "r") as f:
            item = f.read()
        
        name = self.image.filename.split("/")[-1][:-4]

        image_1 = Image.new("RGBA", (self.image.width, self.image.width))
        image_1.paste(self.image)
        image_1.alpha_composite(self.layer1)
        image_1.save(f"{self.block_texture_dir}/slimy_{name}_1.png")
        image_2 = Image.new("RGBA", (self.image.width, self.image.width))
        image_2.paste(self.image)
        image_2.alpha_composite(self.layer2)
        image_2.save(f"{self.block_texture_dir}/slimy_{name}_2.png")
        image_3 = Image.new("RGBA", (self.image.width, self.image.width))
        image_3.paste(self.image)
        image_3.alpha_composite(self.layer3)
        image_3.save(f"{self.block_texture_dir}/slimy_{name}_3.png")
        image_4 = Image.new("RGBA", (self.image.width, self.image.width))
        image_4.paste(self.image)
        image_4.alpha_composite(self.layer4)
        image_4.save(f"{self.block_texture_dir}/slimy_{name}_4.png")

        with open(f"{self.item_dir}/slimy_{name}.json", "w") as f:
            f.write(item.replace("A", name))
        with open(f"{self.item_model_dir}/slimy_{name}.json", "w") as f:
            f.write(item_model.replace("A", name))
        with open(f"{self.blockstate_dir}/slimy_{name}.json", "w") as f:
            f.write(block.replace("A", name))
        with open(f"{self.block_model_dir}/slimy_{name}_1.json", "w") as f:
            f.write(block_model1.replace("A", name))
        with open(f"{self.block_model_dir}/slimy_{name}_2.json", "w") as f:
            f.write(block_model2.replace("A", name))
        with open(f"{self.block_model_dir}/slimy_{name}_3.json", "w") as f:
            f.write(block_model3.replace("A", name))
        with open(f"{self.block_model_dir}/slimy_{name}_4.json", "w") as f:
            f.write(block_model4.replace("A", name))
    
    def loadOverlays(self):
        self.layer1 = Image.open("slimy_1.png")
        self.layer2 = Image.open("slimy_2.png")
        self.layer3 = Image.open("slimy_3.png")
        self.layer4 = Image.open("slimy_4.png")
    
    def destroy(self):
        if (self.image != None):
            self.image.close()
        self.layer1.close()
        self.layer2.close()
        self.layer3.close()
        self.layer4.close()

if __name__ == "__main__":
    app = Application()
    app.destroy()