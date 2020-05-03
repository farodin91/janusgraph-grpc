use grpc;
use structopt::StructOpt;
use std::sync::Arc;
use grpc::ClientStub;
use futures::executor;

mod management_grpc;
mod management;

use management_grpc::*;
use management::*;

#[derive(Debug, StructOpt)]
#[structopt(name = "basic")]
struct JanusGraphCmd {
    #[structopt(short = "h", long = "host")]
    host: String,
    #[structopt(default_value = "10182", short = "p", long = "port")]
    port: u16,
    #[structopt(subcommand)]
    cmd: Command
}

#[derive(Debug, StructOpt)]
enum Command {
    Contexts,
    VertexLabel(CommandArgs),
    EdgeLabel(CommandArgs),
}

#[derive(Debug, StructOpt)]
struct CommandArgs {
    #[structopt(subcommand)]
    command_type: CommandType
}

#[derive(Debug, StructOpt)]
enum CommandType {
    Get,
    Add,
    List,
}


fn main() {
    let opt: JanusGraphCmd = JanusGraphCmd::from_args();
    println!("{:#?}", opt);
    let grpc_client = Arc::new(
        grpc::ClientBuilder::new(&opt.host, opt.port)
            .build()
            .unwrap(),
    );
    match opt.cmd {
        Command::Contexts => {
            let client = AccessContextClient::with_client(grpc_client);
        
            let request = GetContextsRequest::new();
            let resp = client
                .get_contexts(grpc::RequestOptions::new(), request)
                .collect();
            println!("{:#?}", executor::block_on(resp));
        }
        Command::VertexLabel(_) => {
            let client = ManagementForVertexLabelsClient::with_client(grpc_client);
        
            let mut request = GetVertexLabelsRequest::new();
            let mut context = JanusGraphContext::new();
            context.set_graphName("graph".to_string());
            request.set_context(context);
            let resp = client
                .get_vertex_labels(grpc::RequestOptions::new(), request)
                .collect();
            println!("{:?}", executor::block_on(resp));
        }
        Command::EdgeLabel(_) => {
            let client = ManagementForEdgeLabelsClient::with_client(grpc_client);
        
            let mut request = GetEdgeLabelsRequest::new();
            let mut context = JanusGraphContext::new();
            context.set_graphName("graph".to_string());
            request.set_context(context);
            let resp = client
                .get_edge_labels(grpc::RequestOptions::new(), request)
                .collect();
            println!("{:?}", executor::block_on(resp));
        }
    }
}