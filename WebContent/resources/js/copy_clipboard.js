function copy(texto){
	 var textArea = document.createElement("textarea");
	  textArea.style.position = 'fixed';
	  textArea.style.top = 0;
	  textArea.style.left = 0;
	  textArea.style.width = '2em';
	  textArea.style.height = '2em';
	  textArea.style.padding = 0;
	  textArea.style.border = 'none';
	  textArea.style.outline = 'none';
	  textArea.style.boxShadow = 'none';

	  textArea.style.background = 'transparent';
	  textArea.value=texto;
	  document.body.appendChild(textArea);
	  textArea.select();
	  document.execCommand("Copy");
	  console.log(textArea);
	  document.body.removeChild(textArea);
	}

function copy_date(texto){
	 var textArea = document.createElement("textarea");
	 var array= texto.split('@');
	 texto=array.join(" ");
	  textArea.style.position = 'fixed';
	  textArea.style.top = 0;
	  textArea.style.left = 0;
	  textArea.style.width = '2em';
	  textArea.style.height = '2em';
	  textArea.style.padding = 0;
	  textArea.style.border = 'none';
	  textArea.style.outline = 'none';
	  textArea.style.boxShadow = 'none';

	  textArea.style.background = 'transparent';
	  textArea.value=texto;
	  document.body.appendChild(textArea);
	  textArea.select();
	  document.execCommand("Copy");
	  console.log(textArea);
	  document.body.removeChild(textArea);
	}