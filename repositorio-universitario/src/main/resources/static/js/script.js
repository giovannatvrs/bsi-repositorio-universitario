
const listaDisciplinas = document.getElementById('lista-disciplinas');
const btnDisciplinas = document.getElementById('btn-disciplinas');
const botaoNovoArquivo = document.getElementById('novo');
const popUp = document.getElementById('overlay');
const fechar = document.getElementById('close');
const cancelar = document.getElementById('cancelar');
if(listaDisciplinas){
    
    btnDisciplinas.addEventListener('click', function(){
        listaDisciplinas.classList.toggle('ativa');
    })
}

if(popUp){
    novo.addEventListener('click', function(){
        popUp.classList.toggle('ativo');
    })
    fechar.addEventListener('click', function(){
        popUp.classList.remove('ativo');
    })
    cancelar.addEventListener('click', function(){
        popUp.classList.remove('ativo');
    })

}



async function fetchData(){

    try{
        const response = await fetch("/usuario-logado",{
            credentials: 'include'
        });

        if(!response.ok){
            if(response.status == 401){
                window.location.href = "/login";
            }
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        const data= await response.json();
        console.log(data);

        atualizarPerfil(data);
        atualizarNavegacao(data)

    }catch(error){
        console.error("Falha em carregar os dados do usuário",error);

    }


}

function atualizarPerfil(data){
    const fotoDePerfil = document.getElementById("foto-perfil");
    const nomeUsuario = document.getElementById("nome-usuario");
    if(fotoDePerfil) {
        fotoDePerfil.src = data.picture || "default.png";
    }
    if(nomeUsuario){
        nomeUsuario.textContent = data.name;
    }
}

document.addEventListener("DOMContentLoaded", fetchData);



function atualizarNavegacao(data){
    var urlData= window.location.pathname;
    const paginas = document.getElementById("paginas");
    if (urlData == "/usuario.html"){
        if(data.funcao=="ADMINISTRADOR"){
            paginas.innerHTML = `<a>Histórico</a>
            <a href="envios.html">Envios</a>
            <a href="/usuarios.html">Usuários</a>
            <a href="/moderadores.html">Moderadores</a>`
        }
        else if (data.funcao == "MODERADOR"){
            paginas.innerHTML = `<a href="envios.html">Envios</a>
            <a href="/solicitacoes.html">Solicitações</a>
            <a>Restrições</a>`
        }
        else{
            paginas.innerHTML = `<a href="envios.html">Envios</a>`
        }

    }
    else if(urlData == "/usuarios.html"){
        paginas.innerHTML = `<a onclick="history.back()">Voltar</a>
        <a>Histórico</a>
        <a href="/moderadores.html">Moderadores</a>`
    }
    else if(urlData == "/moderadores.html"){
        paginas.innerHTML = `<a onclick="history.back()">Voltar</a>
        <a>Histórico</a>
        <a href="/usuarios.html">Usuários</a>`
    }
    else if(urlData == "/solicitacoes.html"){
        paginas.innerHTML = `<a onclick="history.back()">Voltar</a>
        <a href="envios.html">Envios</a>
        <a href="/usuarios.html">Restrições</a>`
    }

}

if (window.location.pathname == '/envios.html'){
    async function listarArquivosUsuario(){
        try{
            const response = await fetch("/envios", {
                credentials: "include"
            });
            if(!response.ok){
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            const data = await response.json();
            let info = '';

            data.forEach(function (arquivo){
                info += `<tr><td>${arquivo.nome}</td><td>${arquivo.disciplina}</td>
                        <td>${arquivo.status}</td><td><span class="material-symbols-outlined botao-visualizar" data-id="${arquivo.id}">
                          description</span></td>
                          <td><span class="material-symbols-outlined botao-download" data-id="${arquivo.id}" data-nome="${arquivo.nome_real_arquivo}"">download</span></td>
                          <td><span class="material-symbols-outlined deletar-arquivo" data-id="${arquivo.id}">delete</span></td>
                        </tr>`;
            })
            document.getElementById("lista-arquivos-usuario").innerHTML = info;
            downloadArquivo();
            visualizarArquivo();
            deletarArquivo();
        }catch (error){
            console.error("Falha em carregar arquivos do usuário", error);
        }
    }
    document.addEventListener("DOMContentLoaded", listarArquivosUsuario);
}

if (window.location.pathname == '/solicitacoes.html'){
    async function listarArquivosPendentes(){
        try{
            const response = await fetch("/solicitacoes",{
                credentials: "include"
            });
            if(!response.ok){
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            const data = await response.json();
            let info = '';
            data.forEach(function (arquivo){
                info +=`<tr><td>${arquivo.nome}</td><td>${arquivo.disciplina}</td><td>${arquivo.data}</td>
                        <td><span class="material-symbols-outlined botao-visualizar" data-id="${arquivo.id}">description</span></td>
                        <td><button class="aprovar-arquivo" data-id="${arquivo.id}">Aprovar</button></td>
                        <td><button class="reprovar-arquivo" data-id="${arquivo.id}">Reprovar</button></td>
                        </tr>`
            })
            document.getElementById("lista-arquivos-pendentes").innerHTML = info;
            visualizarArquivo();
            aprovarArquivo();
            reprovarArquivo();
        }catch (error){
            console.error("Falha em carregar arquivos", error);
        }
    }

    document.addEventListener("DOMContentLoaded", listarArquivosPendentes);
}

function reprovarArquivo(){
    document.querySelectorAll(".reprovar-arquivo").forEach(icon => {
        icon.addEventListener('click', async()=>{
            const fileId = icon.getAttribute('data-id');
            try{
                const response = await fetch(`/reprovar/${fileId}`, {
                    method: 'POST',
                    credentials: "include"
                })
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Arquivo reprovado");
                window.location.reload();
            }catch(error){
                console.error("Falha em reprovar arquivo", error);
            }
        });
    });
}

function aprovarArquivo(){
    document.querySelectorAll(".aprovar-arquivo").forEach(icon =>{
        icon.addEventListener('click', async() => {
            const fileId = icon.getAttribute('data-id');
            try{
                const response = await fetch(`/aprovar/${fileId}`, {
                    method: 'POST',
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Arquivo aprovado com sucesso!");
                window.location.reload();
            }catch(error){
                console.error("Falha em aprovar arquivo", error);
            }
        });
    });
}

function deletarArquivo(){
    document.querySelectorAll(".deletar-arquivo").forEach(icon => {
        icon.addEventListener('click', async() =>{
            const fileId = icon.getAttribute('data-id');
            try{
                const response = await fetch(`/deletar/${fileId}`,{
                    method:'DELETE',
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Arquivo deletado!");
                window.location.reload();
            }catch(error){
                console.error("Falha em deletar arquivo", error);
            }

        })
        
    })
}

function downloadArquivo(){
    document.querySelectorAll(".botao-download").forEach(icon =>{
        icon.addEventListener('click', async() => {
            const fileId = icon.getAttribute('data-id');
            const fileName = icon.getAttribute('data-nome');
            try{
                const response = await fetch(`/download/${fileId}`, {
                    method: 'GET',
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = fileName || 'arquivo';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
            }catch (error){
                console.error("Falha em realizar o download", error);
            }
        })
    })
}

function visualizarArquivo(){
    document.querySelectorAll(".botao-visualizar").forEach(icon =>{
        icon.addEventListener('click', async() => {
            const fileId = icon.getAttribute('data-id');
            window.open(`/visualizar/${fileId}`, '_blank');
        });
    });
}

async function listarUsuarios(){
    try{
        const response = await fetch("/usuarios",{
            credentials: 'include'
        });
        if(!response.ok){
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        const data = await response.json();
        let info = '';
        data.forEach(function(usuario){
            let papel = '';
            switch(usuario.funcao){
                case "ADMINISTRADOR":
                    papel = 'Administrador(a)';
                    break;
                case "MODERADOR":
                    papel = 'Moderador(a)';
                    break;
                case "USUARIO":
                    papel = 'Usuário(a)';
                    break;
            }

            info +=`<tr><td><div class="informacoes-perfil"><img src=${usuario.url_foto}>${usuario.nome}</div></td>
            <td>${usuario.email}</td><td>${papel}</td>
            
            <td class="alterar-usuario"> ${papel === "Usuário(a)" ? `<button class="botao-tornar-usuario-moderador" data-id="${usuario.id}">Tornar usuário moderador</button>` : ``}</td></tr>`

        })
        document.getElementById("lista-usuarios").innerHTML=info;
        ativarPromocaoUsuarios();
    }
    catch (error){
        console.error("Falha em carregar os dados dos usuários",error);
    }


}
document.addEventListener("DOMContentLoaded", listarUsuarios);

async function listarModeradores(){
    try{
        const response = await fetch("/moderadores", {
            credentials: "include"
        });
        if(!response.ok){
            throw new Error(`Erro HTTP: ${response.status}`);
        }
        const data = await response.json();
        let info = '';
        data.forEach(function (usuario){
            info = `<tr><td><div class="informacoes-perfil"><img src="${usuario.url_foto}">${usuario.nome}</div></td>
                    <td>${usuario.email}</td><td><button class="botao-retirar-papel-moderador" data-id="${usuario.id}">Tirar papel moderador</button></td></tr>`
        })
        document.getElementById("lista-moderadores").innerHTML = info;
        ativarDesligamentoModerador();
    }catch (error){
        console.error("Falha em carregar os dados dos moderadores", error);
    }
}
document.addEventListener("DOMContentLoaded", listarModeradores);




function ativarPromocaoUsuarios(){
    document.querySelectorAll('.botao-tornar-usuario-moderador').forEach(button =>{
        button.addEventListener('click', async () =>{
            const userId = button.getAttribute('data-id');
            try{
                const response = await fetch(`/promover/${userId}`, {
                    method: "POST",
                    credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Usuário(a) promovido com sucesso");
                listarUsuarios();
            }catch(error){
                console.error("Falha em promover usuário para moderador", error);
            }
        });
    });
}

function ativarDesligamentoModerador(){
    document.querySelectorAll('.botao-retirar-papel-moderador').forEach(button =>{
        button.addEventListener('click', async () =>{
            const userId = button.getAttribute('data-id');
            try{
                const response = await fetch(`/retirar-papel/${userId}`, {
                   method: 'POST',
                   credentials: "include"
                });
                if(!response.ok){
                    throw new Error(`Erro HTTP: ${response.status}`);
                }
                alert("Papel de moderador retirado do usuário com sucesso");
                listarModeradores();
            }catch(error){
                console.error("Erro em retirar papel do moderador", error);
            }
        })
    });
}

function fazerUploadArquivo(){
    const upload = document.getElementById("confirmar");
    upload.addEventListener("click", async () =>{
        try{
            let arquivo = document.getElementById("arquivo");
            let nomeArquivo = document.getElementById("nome-arquivo");
            let disciplina = document.getElementById("disciplina");
            let descricao = document.getElementById("descricao-arquivo");

            const formData = new FormData;
            formData.append("file", arquivo.files[0]);
            formData.append("nome", nomeArquivo.value);
            formData.append("disciplina", disciplina.value);
            formData.append("descricao", descricao.value);
            const response = await fetch(`/upload`, {
               method: 'POST',
                credentials: "include",
                body: formData
            });
            if(!response.ok){
                throw new Error(`Erro HTTP: ${response.status}`);
            }
            arquivo.value = '';
            nomeArquivo.value = '';
            disciplina.value = '';
            descricao.value = '';
            alert("Arquivo enviado para avaliação");
            window.location.reload();

        }catch (error){
            console.error("Erro em realizar upload de arquivo", error);
        }
    });
}

fazerUploadArquivo();