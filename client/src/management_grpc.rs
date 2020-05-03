// This file is generated. Do not edit
// @generated

// https://github.com/Manishearth/rust-clippy/issues/702
#![allow(unknown_lints)]
#![allow(clippy)]

#![cfg_attr(rustfmt, rustfmt_skip)]

#![allow(box_pointers)]
#![allow(dead_code)]
#![allow(missing_docs)]
#![allow(non_camel_case_types)]
#![allow(non_snake_case)]
#![allow(non_upper_case_globals)]
#![allow(trivial_casts)]
#![allow(unsafe_code)]
#![allow(unused_imports)]
#![allow(unused_results)]


// server interface

pub trait AccessContext {
    fn get_contexts(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::GetContextsRequest>, resp: ::grpc::ServerResponseSink<super::management::JanusGraphContext>) -> ::grpc::Result<()>;

    fn get_context_by_graph_name(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::GetContextByGraphNameRequest>, resp: ::grpc::ServerResponseUnarySink<super::management::JanusGraphContext>) -> ::grpc::Result<()>;
}

// client

pub struct AccessContextClient {
    grpc_client: ::std::sync::Arc<::grpc::Client>,
}

impl ::grpc::ClientStub for AccessContextClient {
    fn with_client(grpc_client: ::std::sync::Arc<::grpc::Client>) -> Self {
        AccessContextClient {
            grpc_client: grpc_client,
        }
    }
}

impl AccessContextClient {
    pub fn get_contexts(&self, o: ::grpc::RequestOptions, req: super::management::GetContextsRequest) -> ::grpc::StreamingResponse<super::management::JanusGraphContext> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.AccessContext/GetContexts"),
            streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_server_streaming(o, req, descriptor)
    }

    pub fn get_context_by_graph_name(&self, o: ::grpc::RequestOptions, req: super::management::GetContextByGraphNameRequest) -> ::grpc::SingleResponse<super::management::JanusGraphContext> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.AccessContext/GetContextByGraphName"),
            streaming: ::grpc::rt::GrpcStreaming::Unary,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_unary(o, req, descriptor)
    }
}

// server

pub struct AccessContextServer;


impl AccessContextServer {
    pub fn new_service_def<H : AccessContext + 'static + Sync + Send + 'static>(handler: H) -> ::grpc::rt::ServerServiceDefinition {
        let handler_arc = ::std::sync::Arc::new(handler);
        ::grpc::rt::ServerServiceDefinition::new("/grpc.AccessContext",
            vec![
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.AccessContext/GetContexts"),
                        streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerServerStreaming::new(move |ctx, req, resp| (*handler_copy).get_contexts(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.AccessContext/GetContextByGraphName"),
                        streaming: ::grpc::rt::GrpcStreaming::Unary,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerUnary::new(move |ctx, req, resp| (*handler_copy).get_context_by_graph_name(ctx, req, resp))
                    },
                ),
            ],
        )
    }
}

// server interface

pub trait ManagementForEdgeLabels {
    fn get_edge_labels(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::GetEdgeLabelsRequest>, resp: ::grpc::ServerResponseSink<super::management::EdgeLabel>) -> ::grpc::Result<()>;

    fn get_edge_labels_by_name(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::GetEdgeLabelsByNameRequest>, resp: ::grpc::ServerResponseSink<super::management::EdgeLabel>) -> ::grpc::Result<()>;

    fn ensure_edge_label(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::EnsureEdgeLabelRequest>, resp: ::grpc::ServerResponseUnarySink<super::management::EdgeLabel>) -> ::grpc::Result<()>;

    fn get_composite_indices_by_edge_label(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::GetCompositeIndicesByEdgeLabelRequest>, resp: ::grpc::ServerResponseSink<super::management::CompositeEdgeIndex>) -> ::grpc::Result<()>;

    fn ensure_composite_index_by_edge_label(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::EnsureCompositeIndexByEdgeLabelRequest>, resp: ::grpc::ServerResponseUnarySink<super::management::CompositeEdgeIndex>) -> ::grpc::Result<()>;

    fn get_mixed_indices_by_edge_label(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::GetMixedIndicesByEdgeLabelRequest>, resp: ::grpc::ServerResponseSink<super::management::MixedEdgeIndex>) -> ::grpc::Result<()>;

    fn ensure_mixed_index_by_edge_label(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::EnsureMixedIndexByEdgeLabelRequest>, resp: ::grpc::ServerResponseUnarySink<super::management::MixedEdgeIndex>) -> ::grpc::Result<()>;
}

// client

pub struct ManagementForEdgeLabelsClient {
    grpc_client: ::std::sync::Arc<::grpc::Client>,
}

impl ::grpc::ClientStub for ManagementForEdgeLabelsClient {
    fn with_client(grpc_client: ::std::sync::Arc<::grpc::Client>) -> Self {
        ManagementForEdgeLabelsClient {
            grpc_client: grpc_client,
        }
    }
}

impl ManagementForEdgeLabelsClient {
    pub fn get_edge_labels(&self, o: ::grpc::RequestOptions, req: super::management::GetEdgeLabelsRequest) -> ::grpc::StreamingResponse<super::management::EdgeLabel> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/GetEdgeLabels"),
            streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_server_streaming(o, req, descriptor)
    }

    pub fn get_edge_labels_by_name(&self, o: ::grpc::RequestOptions, req: super::management::GetEdgeLabelsByNameRequest) -> ::grpc::StreamingResponse<super::management::EdgeLabel> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/GetEdgeLabelsByName"),
            streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_server_streaming(o, req, descriptor)
    }

    pub fn ensure_edge_label(&self, o: ::grpc::RequestOptions, req: super::management::EnsureEdgeLabelRequest) -> ::grpc::SingleResponse<super::management::EdgeLabel> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/EnsureEdgeLabel"),
            streaming: ::grpc::rt::GrpcStreaming::Unary,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_unary(o, req, descriptor)
    }

    pub fn get_composite_indices_by_edge_label(&self, o: ::grpc::RequestOptions, req: super::management::GetCompositeIndicesByEdgeLabelRequest) -> ::grpc::StreamingResponse<super::management::CompositeEdgeIndex> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/GetCompositeIndicesByEdgeLabel"),
            streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_server_streaming(o, req, descriptor)
    }

    pub fn ensure_composite_index_by_edge_label(&self, o: ::grpc::RequestOptions, req: super::management::EnsureCompositeIndexByEdgeLabelRequest) -> ::grpc::SingleResponse<super::management::CompositeEdgeIndex> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/EnsureCompositeIndexByEdgeLabel"),
            streaming: ::grpc::rt::GrpcStreaming::Unary,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_unary(o, req, descriptor)
    }

    pub fn get_mixed_indices_by_edge_label(&self, o: ::grpc::RequestOptions, req: super::management::GetMixedIndicesByEdgeLabelRequest) -> ::grpc::StreamingResponse<super::management::MixedEdgeIndex> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/GetMixedIndicesByEdgeLabel"),
            streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_server_streaming(o, req, descriptor)
    }

    pub fn ensure_mixed_index_by_edge_label(&self, o: ::grpc::RequestOptions, req: super::management::EnsureMixedIndexByEdgeLabelRequest) -> ::grpc::SingleResponse<super::management::MixedEdgeIndex> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/EnsureMixedIndexByEdgeLabel"),
            streaming: ::grpc::rt::GrpcStreaming::Unary,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_unary(o, req, descriptor)
    }
}

// server

pub struct ManagementForEdgeLabelsServer;


impl ManagementForEdgeLabelsServer {
    pub fn new_service_def<H : ManagementForEdgeLabels + 'static + Sync + Send + 'static>(handler: H) -> ::grpc::rt::ServerServiceDefinition {
        let handler_arc = ::std::sync::Arc::new(handler);
        ::grpc::rt::ServerServiceDefinition::new("/grpc.ManagementForEdgeLabels",
            vec![
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/GetEdgeLabels"),
                        streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerServerStreaming::new(move |ctx, req, resp| (*handler_copy).get_edge_labels(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/GetEdgeLabelsByName"),
                        streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerServerStreaming::new(move |ctx, req, resp| (*handler_copy).get_edge_labels_by_name(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/EnsureEdgeLabel"),
                        streaming: ::grpc::rt::GrpcStreaming::Unary,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerUnary::new(move |ctx, req, resp| (*handler_copy).ensure_edge_label(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/GetCompositeIndicesByEdgeLabel"),
                        streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerServerStreaming::new(move |ctx, req, resp| (*handler_copy).get_composite_indices_by_edge_label(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/EnsureCompositeIndexByEdgeLabel"),
                        streaming: ::grpc::rt::GrpcStreaming::Unary,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerUnary::new(move |ctx, req, resp| (*handler_copy).ensure_composite_index_by_edge_label(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/GetMixedIndicesByEdgeLabel"),
                        streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerServerStreaming::new(move |ctx, req, resp| (*handler_copy).get_mixed_indices_by_edge_label(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForEdgeLabels/EnsureMixedIndexByEdgeLabel"),
                        streaming: ::grpc::rt::GrpcStreaming::Unary,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerUnary::new(move |ctx, req, resp| (*handler_copy).ensure_mixed_index_by_edge_label(ctx, req, resp))
                    },
                ),
            ],
        )
    }
}

// server interface

pub trait ManagementForVertexLabels {
    fn get_vertex_labels(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::GetVertexLabelsRequest>, resp: ::grpc::ServerResponseSink<super::management::VertexLabel>) -> ::grpc::Result<()>;

    fn get_vertex_labels_by_name(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::GetVertexLabelsByNameRequest>, resp: ::grpc::ServerResponseSink<super::management::VertexLabel>) -> ::grpc::Result<()>;

    fn ensure_vertex_label(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::EnsureVertexLabelRequest>, resp: ::grpc::ServerResponseUnarySink<super::management::VertexLabel>) -> ::grpc::Result<()>;

    fn get_composite_indices_by_vertex_label(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::GetCompositeIndicesByVertexLabelRequest>, resp: ::grpc::ServerResponseSink<super::management::CompositeVertexIndex>) -> ::grpc::Result<()>;

    fn ensure_composite_index_by_vertex_label(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::EnsureCompositeIndexByVertexLabelRequest>, resp: ::grpc::ServerResponseUnarySink<super::management::CompositeVertexIndex>) -> ::grpc::Result<()>;

    fn get_mixed_indices_by_vertex_label(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::GetMixedIndicesByVertexLabelRequest>, resp: ::grpc::ServerResponseSink<super::management::MixedVertexIndex>) -> ::grpc::Result<()>;

    fn ensure_mixed_index_by_vertex_label(&self, o: ::grpc::ServerHandlerContext, req: ::grpc::ServerRequestSingle<super::management::EnsureMixedIndexByVertexLabelRequest>, resp: ::grpc::ServerResponseUnarySink<super::management::MixedVertexIndex>) -> ::grpc::Result<()>;
}

// client

pub struct ManagementForVertexLabelsClient {
    grpc_client: ::std::sync::Arc<::grpc::Client>,
}

impl ::grpc::ClientStub for ManagementForVertexLabelsClient {
    fn with_client(grpc_client: ::std::sync::Arc<::grpc::Client>) -> Self {
        ManagementForVertexLabelsClient {
            grpc_client: grpc_client,
        }
    }
}

impl ManagementForVertexLabelsClient {
    pub fn get_vertex_labels(&self, o: ::grpc::RequestOptions, req: super::management::GetVertexLabelsRequest) -> ::grpc::StreamingResponse<super::management::VertexLabel> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/GetVertexLabels"),
            streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_server_streaming(o, req, descriptor)
    }

    pub fn get_vertex_labels_by_name(&self, o: ::grpc::RequestOptions, req: super::management::GetVertexLabelsByNameRequest) -> ::grpc::StreamingResponse<super::management::VertexLabel> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/GetVertexLabelsByName"),
            streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_server_streaming(o, req, descriptor)
    }

    pub fn ensure_vertex_label(&self, o: ::grpc::RequestOptions, req: super::management::EnsureVertexLabelRequest) -> ::grpc::SingleResponse<super::management::VertexLabel> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/EnsureVertexLabel"),
            streaming: ::grpc::rt::GrpcStreaming::Unary,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_unary(o, req, descriptor)
    }

    pub fn get_composite_indices_by_vertex_label(&self, o: ::grpc::RequestOptions, req: super::management::GetCompositeIndicesByVertexLabelRequest) -> ::grpc::StreamingResponse<super::management::CompositeVertexIndex> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/GetCompositeIndicesByVertexLabel"),
            streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_server_streaming(o, req, descriptor)
    }

    pub fn ensure_composite_index_by_vertex_label(&self, o: ::grpc::RequestOptions, req: super::management::EnsureCompositeIndexByVertexLabelRequest) -> ::grpc::SingleResponse<super::management::CompositeVertexIndex> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/EnsureCompositeIndexByVertexLabel"),
            streaming: ::grpc::rt::GrpcStreaming::Unary,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_unary(o, req, descriptor)
    }

    pub fn get_mixed_indices_by_vertex_label(&self, o: ::grpc::RequestOptions, req: super::management::GetMixedIndicesByVertexLabelRequest) -> ::grpc::StreamingResponse<super::management::MixedVertexIndex> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/GetMixedIndicesByVertexLabel"),
            streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_server_streaming(o, req, descriptor)
    }

    pub fn ensure_mixed_index_by_vertex_label(&self, o: ::grpc::RequestOptions, req: super::management::EnsureMixedIndexByVertexLabelRequest) -> ::grpc::SingleResponse<super::management::MixedVertexIndex> {
        let descriptor = ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
            name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/EnsureMixedIndexByVertexLabel"),
            streaming: ::grpc::rt::GrpcStreaming::Unary,
            req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
            resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
        });
        self.grpc_client.call_unary(o, req, descriptor)
    }
}

// server

pub struct ManagementForVertexLabelsServer;


impl ManagementForVertexLabelsServer {
    pub fn new_service_def<H : ManagementForVertexLabels + 'static + Sync + Send + 'static>(handler: H) -> ::grpc::rt::ServerServiceDefinition {
        let handler_arc = ::std::sync::Arc::new(handler);
        ::grpc::rt::ServerServiceDefinition::new("/grpc.ManagementForVertexLabels",
            vec![
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/GetVertexLabels"),
                        streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerServerStreaming::new(move |ctx, req, resp| (*handler_copy).get_vertex_labels(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/GetVertexLabelsByName"),
                        streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerServerStreaming::new(move |ctx, req, resp| (*handler_copy).get_vertex_labels_by_name(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/EnsureVertexLabel"),
                        streaming: ::grpc::rt::GrpcStreaming::Unary,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerUnary::new(move |ctx, req, resp| (*handler_copy).ensure_vertex_label(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/GetCompositeIndicesByVertexLabel"),
                        streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerServerStreaming::new(move |ctx, req, resp| (*handler_copy).get_composite_indices_by_vertex_label(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/EnsureCompositeIndexByVertexLabel"),
                        streaming: ::grpc::rt::GrpcStreaming::Unary,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerUnary::new(move |ctx, req, resp| (*handler_copy).ensure_composite_index_by_vertex_label(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/GetMixedIndicesByVertexLabel"),
                        streaming: ::grpc::rt::GrpcStreaming::ServerStreaming,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerServerStreaming::new(move |ctx, req, resp| (*handler_copy).get_mixed_indices_by_vertex_label(ctx, req, resp))
                    },
                ),
                ::grpc::rt::ServerMethod::new(
                    ::grpc::rt::ArcOrStatic::Static(&::grpc::rt::MethodDescriptor {
                        name: ::grpc::rt::StringOrStatic::Static("/grpc.ManagementForVertexLabels/EnsureMixedIndexByVertexLabel"),
                        streaming: ::grpc::rt::GrpcStreaming::Unary,
                        req_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                        resp_marshaller: ::grpc::rt::ArcOrStatic::Static(&::grpc_protobuf::MarshallerProtobuf),
                    }),
                    {
                        let handler_copy = handler_arc.clone();
                        ::grpc::rt::MethodHandlerUnary::new(move |ctx, req, resp| (*handler_copy).ensure_mixed_index_by_vertex_label(ctx, req, resp))
                    },
                ),
            ],
        )
    }
}
